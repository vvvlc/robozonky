/*
 * Copyright 2017 The RoboZonky Project
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

package com.github.robozonky.api.confirmations;

import java.util.Optional;
import java.util.ServiceLoader;

/**
 * Use Java's {@link ServiceLoader} to load different confirmation providers.
 */
public interface ConfirmationProviderService {

    /**
     * Prepare the confirmation for being used by the app.
     * @param providerId ID of the confirmation provider.
     * @return Instance of the confirmation provider, ready for accepting confirmation requests, if the service
     * supports it.
     */
    Optional<ConfirmationProvider> find(String providerId);
}
