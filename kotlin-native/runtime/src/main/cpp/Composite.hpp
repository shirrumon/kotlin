/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#pragma once

#include <algorithm>
#include <cstddef>

#include "Alignment.hpp"

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
        }(baseOffset, fieldDescriptors...);
    }
}

}

// Get offset of a field in a composite type composed of `FieldDescriptors...`.
//
// `index` can be equal to count of `FieldDescriptors...` This will point to
// right after the last field.
template <size_t index, typename... FieldDescriptors>
constexpr size_t fieldOffset(FieldDescriptors... fieldDescriptors) noexcept {
    return internal::fieldOffsetFromBase<index>(0, fieldDescriptors...);
}

// Get alignment of a composite type composed of `FieldDescriptors...`.
// This will be the maximum alignment across all fields.
// Alignment of an empty composite type is 1.
template <typename... FieldDescriptors>
constexpr size_t alignment(FieldDescriptors... fieldDescriptors) noexcept {
    if constexpr (sizeof...(FieldDescriptors) == 0) {
        return 1;
    } else {
        return [](auto head, auto... tail) constexpr noexcept {
            return std::max(head.alignment(), alignment(tail...));
        }(fieldDescriptors...);
    }
}

// Get size of a composite type composed of `FieldDescriptors...`.
// The size of an empty composite type will be 0, unlike C++ where it's 1.
template <typename... FieldDescriptors>
constexpr size_t size(FieldDescriptors... fieldDescriptors) noexcept {
    auto offset = fieldOffset<sizeof...(FieldDescriptors)>(fieldDescriptors...);
    if (offset == 0) {
        return offset;
    } else {
        return AlignUp(offset, alignment(fieldDescriptors...));
    }
}

} // namespace kotlin::composite
