/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef RUNTIME_NATIVES_H
#define RUNTIME_NATIVES_H

#include "Types.h"
#include "Exceptions.h"
#include "Memory.h"
#include "std_support/Span.hpp"

constexpr size_t alignUp(size_t size, size_t alignment) {
  return (size + alignment - 1) & ~(alignment - 1);
}

template <typename T>
inline T* AddressOfElementAt(ArrayHeader* obj, KInt index) {
  int8_t* body = reinterpret_cast<int8_t*>(obj) + alignUp(sizeof(ArrayHeader), alignof(T));
  return reinterpret_cast<T*>(body) + index;
}

template <typename T>
inline const T* AddressOfElementAt(const ArrayHeader* obj, KInt index) {
  const int8_t* body = reinterpret_cast<const int8_t*>(obj) + alignUp(sizeof(ArrayHeader), alignof(T));
  return reinterpret_cast<const T*>(body) + index;
}

// Optimized versions not accessing type info.
inline KByte* ByteArrayAddressOfElementAt(ArrayHeader* obj, KInt index) {
  return AddressOfElementAt<KByte>(obj, index);
}

inline const KByte* ByteArrayAddressOfElementAt(const ArrayHeader* obj, KInt index) {
  return AddressOfElementAt<KByte>(obj, index);
}

inline KChar* CharArrayAddressOfElementAt(ArrayHeader* obj, KInt index) {
  return AddressOfElementAt<KChar>(obj, index);
}

inline const KChar* CharArrayAddressOfElementAt(const ArrayHeader* obj, KInt index) {
  return AddressOfElementAt<KChar>(obj, index);
}

inline KInt* IntArrayAddressOfElementAt(ArrayHeader* obj, KInt index) {
  return AddressOfElementAt<KInt>(obj, index);
}

inline const KInt* IntArrayAddressOfElementAt(const ArrayHeader* obj, KInt index) {
  return AddressOfElementAt<KInt>(obj, index);
}

inline KLong* LongArrayAddressOfElementAt(ArrayHeader* obj, KInt index) {
  return AddressOfElementAt<KLong>(obj, index);
}

inline const KLong* LongArrayAddressOfElementAt(const ArrayHeader* obj, KInt index) {
  return AddressOfElementAt<KLong>(obj, index);
}

// Consider aligning of base to sizeof(T).
template <typename T>
inline T* PrimitiveArrayAddressOfElementAt(ArrayHeader* obj, KInt index) {
  return AddressOfElementAt<T>(obj, index);
}

template <typename T>
inline const T* PrimitiveArrayAddressOfElementAt(const ArrayHeader* obj, KInt index) {
  return AddressOfElementAt<T>(obj, index);
}

inline KRef* ArrayAddressOfElementAt(ArrayHeader* obj, KInt index) {
  return AddressOfElementAt<KRef>(obj, index);
}

inline const KRef* ArrayAddressOfElementAt(const ArrayHeader* obj, KInt index) {
  return AddressOfElementAt<KRef>(obj, index);
}

#ifdef __cplusplus
extern "C" {
#endif

OBJ_GETTER0(TheEmptyString);
void Kotlin_io_Console_println0();
void Kotlin_io_Console_println0ToStdErr();
void Kotlin_NativePtrArray_set(KRef thiz, KInt index, KNativePtr value);
KNativePtr Kotlin_NativePtrArray_get(KConstRef thiz, KInt index);
RUNTIME_NOTHROW RUNTIME_PURE KRef* Kotlin_arrayGetElementAddress(KRef array, KInt index);
RUNTIME_NOTHROW RUNTIME_PURE KInt* Kotlin_intArrayGetElementAddress(KRef array, KInt index);
RUNTIME_NOTHROW RUNTIME_PURE KLong* Kotlin_longArrayGetElementAddress(KRef array, KInt index);

#ifdef __cplusplus
}
#endif

namespace kotlin {

template <typename T = KRef>
std_support::span<T> spanForArray(ArrayHeader* array) noexcept {
    return std_support::span<T>(AddressOfElementAt<T>(array, 0), array->count_);
}

template <typename T = KRef>
std_support::span<const T> spanForArray(const ArrayHeader* array) noexcept {
    return std_support::span<const T>(AddressOfElementAt<T>(array, 0), array->count_);
}

inline std_support::span<KByte> spanForByteArray(ArrayHeader* array) noexcept {
    return spanForArray<KByte>(array);
}

inline std_support::span<const KByte> spanForByteArray(const ArrayHeader* array) noexcept {
    return spanForArray<const KByte>(array);
}

inline std_support::span<KChar> spanForCharArray(ArrayHeader* array) noexcept {
    return spanForArray<KChar>(array);
}

inline std_support::span<const KChar> spanForCharArray(const ArrayHeader* array) noexcept {
    return spanForArray<const KChar>(array);
}

inline std_support::span<KInt> spanForIntArray(ArrayHeader* array) noexcept {
    return spanForArray<KInt>(array);
}

inline std_support::span<const KInt> spanForIntArray(const ArrayHeader* array) noexcept {
    return spanForArray<const KInt>(array);
}

inline std_support::span<KLong> spanForLongArray(ArrayHeader* array) noexcept {
    return spanForArray<KLong>(array);
}

inline std_support::span<const KLong> spanForLongArray(const ArrayHeader* array) noexcept {
    return spanForArray<const KLong>(array);
}

} // namespace kotlin

#endif // RUNTIME_NATIVES_H
