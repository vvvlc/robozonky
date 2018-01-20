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

package com.github.robozonky.app.management;

import com.github.robozonky.app.runtime.RuntimeHandler;
import org.junit.Test;
import org.mockito.Mockito;

public class RuntimeTest {

    @Test
    public void shutdown() {
        final RuntimeHandler h = Mockito.mock(RuntimeHandler.class);
        final Runtime r = new Runtime(h);
        r.stopDaemon();
        Mockito.verify(h).resumeToShutdown();
    }
}
