/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#pragma once

#include <algorithm>
#include <tuple>

#include "Alignment.hpp"
#include "KAssert.h"
#include "Utils.hpp"

namespace kotlin {

template <typename T, size_t alignAs = alignof(T)>
class Single : private Pinned {
public:
    static_assert(alignAs >= alignof(T), "Must be aligned at least like T");
    static_assert(IsValidAlignment(alignAs), "Must be a valid alignment");

    constexpr static size_t objectSize() noexcept { return AlignUp(sizeof(T), alignAs); }
    constexpr static size_t objectAlignment() noexcept { return alignAs; }

    static Single* reinterpret(void* ptr) noexcept {
        RuntimeAssert(IsAligned(ptr, objectAlignment()), "Pointer %p must be aligned to %zu", ptr, objectAlignment());
        return static_cast<Single*>(ptr);
    }

    constexpr void construct() noexcept { new (this) T(); }

    constexpr void destroy() noexcept { reinterpret_cast<T*>(this)->~T(); }

    template <size_t index>
    constexpr T& get() noexcept;

    template <>
    constexpr T& get<0>() noexcept {
        return *reinterpret_cast<T*>(this);
    }

private:
    Single() = delete;
    ~Single() = delete;
};

template <typename... Components>
class Composite;

template <>
class Composite<> : private Pinned {
public:
    constexpr static size_t objectSize() noexcept { return 0; }
    constexpr static size_t objectAlignment() noexcept { return 1; }
    constexpr static size_t firstFieldAlignment() noexcept { return 1; }

    static Composite* reinterpret(void* ptr) noexcept { return static_cast<Composite*>(ptr); }

    constexpr void construct() noexcept {}
    constexpr void destroy() noexcept {}

private:
    Composite() = delete;
    ~Composite() = delete;
};

template <typename Component, typename... Components>
class Composite<Component, Components...> : private Pinned {
public:
    constexpr static size_t objectSize() noexcept {
        auto headSizeWithPadding = nextFieldOffset();
        auto tailSize = Composite<Components...>::objectSize();
        return headSizeWithPadding + tailSize;
    }

    constexpr static size_t objectAlignment() noexcept {
        return std::max(firstFieldAlignment(), Composite<Components...>::objectAlignment());
    }

    constexpr static size_t firstFieldAlignment() noexcept {
        return Component::objectAlignment();
    }

    static Composite* reinterpret(void* ptr) noexcept {
        RuntimeAssert(IsAligned(ptr, objectAlignment()), "Pointer %p must be aligned to %zu", ptr, objectAlignment());
        return static_cast<Composite*>(ptr);
    }

    void construct() noexcept {
        Component::reinterpret(this)->construct();
        nextField()->construct();
    }

    void destroy() noexcept {
        nextField()->destroy();
        Component::reinterpret(this)->destroy();
    }

    template <size_t index>
    std::tuple_element_t<index, std::tuple<Component, Components...>>& get() noexcept {
        if constexpr (index == 0) {
            return *reinterpret_cast<Component*>(this);
        } else {
            return nextField()->template get<index - 1>();
        }
    }

private:
    constexpr static size_t nextFieldOffset() noexcept {
        return AlignUp(Component::objectSize(), Composite<Components...>::firstFieldAlignment());
    }

    constexpr Composite<Components...>* nextField() noexcept {
        return reinterpret_cast<Composite<Components...>*>(reinterpret_cast<uint8_t*>(this) + nextFieldOffset());
    }

    Composite() = delete;
    ~Composite() = delete;
};

} // namespace kotlin
