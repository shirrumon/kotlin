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

// Descriptor for the regular C++ type.
template <typename T, size_t alignAs = alignof(T)>
struct Reg {
    static_assert(IsValidAlignment(alignAs));
    static_assert(alignAs >= alignof(T));

    static constexpr size_t size() noexcept { return sizeof(T); }
    static constexpr size_t alignment() noexcept { return alignAs; }
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

    // `index` can be equal to count of `Fields...` This will point to
    // right after the last field.
    template <size_t index>
    constexpr size_t fieldOffset() const noexcept {
        return std::apply(internal::fieldOffset<index, Fields...>, fields_);
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

    // `index` can be equal to count of `Fields...` This will point to
    // right after the last field.
    template <size_t index>
    constexpr size_t fieldOffset() const noexcept {
        static_assert(index == 0);
        return 0;
    }
};

} // namespace kotlin::composite
