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
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.vavr.control.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds an instance of {@link Reloadable}.
 * @param <T> The type that the {@link Reloadable} should hold.
 */
public final class ReloadableBuilder<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReloadableBuilder.class);

    private final Supplier<T> supplier;
    private Duration reloadAfter;
    private Consumer<T> finisher;
    private boolean async = false;

    ReloadableBuilder(final Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * When {@link Reloadable#get()} is called after this time passed since the remote operation was executed last,
     * the operation will be executed again. If the operation throws an exception, the reload will fail and there would
     * be no value. If not specified, the {@link Reloadable} will never reload, unless {@link Reloadable#clear()} is
     * called.
     * @param duration
     * @return This.
     */
    public ReloadableBuilder<T> reloadAfter(final Duration duration) {
        this.reloadAfter = duration;
        return this;
    }

    /**
     * When the operation is successfully executed without errors, the given finisher has to be executed as well. If it
     * throws an exception, the operation itself is considered failed.
     * @param consumer
     * @return This.
     */
    public ReloadableBuilder<T> finishWith(final Consumer<T> consumer) {
        this.finisher = consumer;
        return this;
    }

    /**
     * If specified, {@link Reloadable#get()} will only trigger the operation on the background and return the
     * (now stale) value stored previously. If the background operation fails, the stale value will continue to be
     * returned. On the first {@link Reloadable#get()} call, the operation will be performed synchronously, as there
     * would otherwise be no stale value to return.
     * @return This.
     */
    public ReloadableBuilder<T> async() {
        this.async = true;
        return this;
    }

    /**
     * Build an initialized instance. Will call {@link #build()}, immediately following it up with a call to
     * {@link Reloadable#get()}.
     * @return New initialized instance.
     */
    public Either<Throwable, Reloadable<T>> buildEager() {
        final Reloadable<T> result = build();
        LOGGER.debug("Running before returning: {}.", result);
        final Either<Throwable, T> executed = result.get();
        return executed.map(r -> result);
    }

    /**
     * Build an empty instance. It will be initialized, executing the remote operation, whenever
     * {@link Reloadable#get()} is first called.
     * @return New instance.
     */
    public Reloadable<T> build() {
        if (finisher == null) {
            if (reloadAfter == null) {
                return async ?
                        new AsyncReloadableImpl<>(supplier) :
                        new ReloadableImpl<>(supplier);
            } else {
                return async ?
                        new AsyncReloadableImpl<>(supplier, reloadAfter) :
                        new ReloadableImpl<>(supplier, reloadAfter);
            }
        } else {
            if (reloadAfter == null) {
                return async ?
                        new AsyncReloadableImpl<>(supplier, finisher) :
                        new ReloadableImpl<>(supplier, finisher);
            } else {
                return async ?
                        new AsyncReloadableImpl<>(supplier, reloadAfter, finisher) :
                        new ReloadableImpl<>(supplier, reloadAfter, finisher);
            }
        }
    }
}