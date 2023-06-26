/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#pragma once

namespace kotlin::mm {

class ThreadData;

using SafePointAction = void (*)(mm::ThreadData&);

bool trySetSafePointAction(SafePointAction action) noexcept;
void unsetSafePointAction() noexcept;

void safePoint() noexcept;
void safePoint(ThreadData& threadData) noexcept;

} // namespace kotlin::mm
