/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#pragma once

#include <algorithm>
#include <cstddef>
#include <tuple>

#include "Alignment.hpp"
#include "KAssert.h"
#include "RawPtr.hpp"

namespace kotlin::composite {

namespace internal {

template <size_t index, typename... FieldDescriptors>
constexpr size_t fieldOffsetFromBase(size_t baseOffset, FieldDescriptors... fieldDescriptors) noexcept {
    constexpr auto fieldsDescriptorsSize = sizeof...(FieldDescriptors);
    static_assert(index <= fieldsDescriptorsSize);

    if constexpr (fieldsDescriptorsSize == 0) {
        return baseOffset;
    } else {
        return [](size_t baseOffset, auto head, auto... tail) constexpr noexcept {
            baseOffset = AlignUp(baseOffset, head.alignment());
            if constexpr (index == 0) {
                return baseOffset;
            } else {
                return fieldOffsetFromBase<index - 1>(baseOffset + head.size(), tail...);
            }
        }
        (baseOffset, fieldDescriptors...);
    }
}

template <size_t index, typename... FieldDescriptors>
constexpr size_t fieldOffset(FieldDescriptors... fieldDescriptors) noexcept {
    return fieldOffsetFromBase<index>(0, fieldDescriptors...);
}

template <typename... FieldDescriptors>
constexpr size_t alignment(FieldDescriptors... fieldDescriptors) noexcept {
    if constexpr (sizeof...(FieldDescriptors) == 0) {
        return 1;
    } else {
        return [](auto head, auto... tail) constexpr noexcept { return std::max(head.alignment(), alignment(tail...)); }
        (fieldDescriptors...);
    }
}

template <typename... FieldDescriptors>
constexpr size_t size(FieldDescriptors... fieldDescriptors) noexcept {
    auto offset = fieldOffset<sizeof...(FieldDescriptors)>(fieldDescriptors...);
    if (offset == 0) {
        return offset;
    } else {
        return AlignUp(offset, alignment(fieldDescriptors...));
    }
}

} // namespace internal

namespace descriptor {

// Descriptor for the regular C++ type.
template <typename T, size_t alignAs = alignof(T)>
struct Reg {
    static_assert(IsValidAlignment(alignAs));
    static_assert(alignAs >= alignof(T));

    static constexpr size_t size() noexcept {
        return sizeof(T) == 0 ? 0 : AlignUp(sizeof(T), alignment());
    }
    static constexpr size_t alignment() noexcept { return alignAs; }

    static constexpr T* get(void* ptr) noexcept { return static_cast<T*>(ptr); }

    constexpr bool operator==(const Reg& rhs) const noexcept { return true; }
};

// Descriptor for a type which size and alignment are not known at compile time.
class Dynamic {
public:
    Dynamic(size_t size, size_t alignment) : size_(size), alignment_(alignment) {
        RuntimeAssert(IsValidAlignment(alignment_), "%zu must be valid alignment", alignment_);
        RuntimeAssert(IsAligned(size_, alignment_), "%zu must aligned at %zu", size_, alignment_);
    }

    size_t size() const noexcept { return size_; }
    size_t alignment() const noexcept { return alignment_; }

    bool operator==(const Dynamic& rhs) const noexcept { return size_ == rhs.size_ && alignment_ == rhs.alignment_; }

private:
    size_t size_;
    size_t alignment_;
};

// Descriptor for a composite type consisting of `Fields...`.
template <typename... Fields>
class Composite {
public:
    constexpr Composite() noexcept = default;

    constexpr explicit Composite(Fields... fields) noexcept : fields_(fields...) {}

    // This will be the maximum alignment across all fields.
    // Alignment of an empty composite type is 1.
    constexpr size_t alignment() const noexcept { return std::apply(internal::alignment<Fields...>, fields_); }

    // The size of an empty composite type will be 0, unlike C++ where it's 1.
    constexpr size_t size() const noexcept { return std::apply(internal::size<Fields...>, fields_); }

    constexpr bool operator==(const Composite& rhs) const noexcept {
        return fields_ == rhs.fields_;
    }

    // `index` can be equal to count of `Fields...` This will point to
    // right after the last field.
    template <size_t index>
    constexpr size_t fieldOffset() const noexcept {
        return std::apply(internal::fieldOffset<index, Fields...>, fields_);
    }

    template <size_t index>
    constexpr std::pair<std::tuple_element_t<index, std::tuple<Fields...>>, void*> field(void* ptr) noexcept {
        auto offset = fieldOffset<index>();
        auto field = std::get<index>(fields_);
        return { field, static_cast<void*>(reinterpret_cast<uint8_t*>(ptr) + offset) };
    }

    template <size_t index>
    constexpr void* fromField(std::tuple_element_t<index, std::tuple<Fields...>> field, void* ptr) noexcept {
        RuntimeAssert(field == std::get<index>(fields_), "Actual field is different");
        auto offset = fieldOffset<index>();
        return static_cast<void*>(reinterpret_cast<uint8_t*>(ptr) - offset);
    }

private:
    std::tuple<Fields...> fields_;
};

template <>
class Composite<> {
public:
    constexpr Composite() noexcept = default;

    // This will be the maximum alignment across all fields.
    // Alignment of an empty composite type is 1.
    constexpr size_t alignment() const noexcept { return 1; }

    // The size of an empty composite type will be 0, unlike C++ where it's 1.
    constexpr size_t size() const noexcept { return 0; }

    constexpr bool operator==(const Composite& rhs) const noexcept { return true; }

    // `index` can be equal to count of `Fields...` This will point to
    // right after the last field.
    template <size_t index>
    constexpr size_t fieldOffset() const noexcept {
        static_assert(index == 0);
        return 0;
    }
};

}

template <typename D>
class Ref {
public:
    using Descriptor = D;

    constexpr Ref(Descriptor descriptor, void* ptr) noexcept : data_(static_cast<uint8_t*>(ptr)), descriptor_(descriptor) {}

    constexpr Descriptor descriptor() const noexcept { return descriptor_; }

    constexpr void* data() noexcept { return static_cast<uint8_t*>(data_); }

private:
    raw_ptr<uint8_t> data_;
    [[no_unique_address]] Descriptor descriptor_;
};

template <typename T, size_t alignAs>
class Ref<descriptor::Reg<T, alignAs>> {
public:
    using Descriptor = descriptor::Reg<T, alignAs>;

    constexpr Ref(Descriptor descriptor, void* ptr) noexcept : data_(static_cast<uint8_t*>(ptr)) {}

    constexpr Descriptor descriptor() const noexcept { return Descriptor(); }

    constexpr void* data() noexcept { return static_cast<uint8_t*>(data_); }

    constexpr T* get() noexcept { return descriptor().get(data()); }
    constexpr T& operator*() noexcept { return *get(); }
    constexpr T* operator->() noexcept { return get(); }

private:
    raw_ptr<uint8_t> data_;
};

template <typename... Fields>
class Ref<descriptor::Composite<Fields...>> {
public:
    using Descriptor = descriptor::Composite<Fields...>;

    constexpr Ref(Descriptor descriptor, void* ptr) noexcept : data_(static_cast<uint8_t*>(ptr)), descriptor_(descriptor) {}

    constexpr Descriptor descriptor() const noexcept { return descriptor_; }

    constexpr void* data() noexcept { return static_cast<uint8_t*>(data_); }

    template <size_t index>
    constexpr Ref<std::tuple_element_t<index, std::tuple<Fields...>>> get() noexcept {
        auto [descriptor, ptr] = descriptor_.template field<index>(data());
        return Ref<decltype(descriptor)>(descriptor, ptr);
    }

    template <size_t index>
    constexpr static Ref fromField(Descriptor descriptor, Ref<std::tuple_element_t<index, std::tuple<Fields...>>> field) noexcept {
        return Ref(descriptor, descriptor.template fromField<index>(field.descriptor(), field.data()));
    }

private:
    raw_ptr<uint8_t> data_;
    [[no_unique_address]] Descriptor descriptor_;
};

} // namespace kotlin::composite
