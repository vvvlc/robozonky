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

package com.github.robozonky.app.runtime;

import java.time.Duration;

import com.github.robozonky.app.ReturnCode;
import com.github.robozonky.app.ShutdownHook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DaemonShutdownHookTest {

    @Test
    void runtime() {
        final Lifecycle lifecycle = mock(Lifecycle.class);
        final ShutdownEnabler se = mock(ShutdownEnabler.class);
        final DaemonShutdownHook hook = new DaemonShutdownHook(lifecycle, se);
        hook.start();
        se.get().ifPresent(c -> c.accept(new ShutdownHook.Result(ReturnCode.OK, null)));
        Assertions.assertTimeout(Duration.ofSeconds(5), () -> {
            while (hook.isAlive()) { // wait until the hook to terminate
                Thread.sleep(1);
            }
            verify(se).waitUntilTriggered();
            verify(lifecycle).resumeToShutdown();
        });
    }
}
