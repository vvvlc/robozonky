/*
 * Copyright 2018 The RoboZonky Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.robozonky.common.async;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import com.github.robozonky.internal.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class TimeBasedReload implements ReloadDetection {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeBasedReload.class);

    private final AtomicReference<Instant> lastReloaded;
    private final Duration reloadAfter;

    public TimeBasedReload(final Duration reloadAfter) {
        this.reloadAfter = reloadAfter;
        lastReloaded = new AtomicReference<>();
    }

    @Override
    public boolean getAsBoolean() {
        final Instant lastReloadedInstant = lastReloaded.get();
        return lastReloadedInstant == null ||
                lastReloadedInstant.plus(reloadAfter).isBefore(DateUtil.now());
    }

    @Override
    public void markReloaded() {
        lastReloaded.set(DateUtil.now());
        LOGGER.trace("Marked reloaded on {}.", this);
    }

    @Override
    public void forceReload() {
        lastReloaded.set(null);
        LOGGER.trace("Forcing reload on {}.", this);
    }
}