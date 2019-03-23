/*
 * Copyright 2019 The RoboZonky Project
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

package com.github.robozonky.api.remote.entities.sanitized;

import com.github.robozonky.api.remote.entities.MyReservation;
import com.github.robozonky.api.remote.entities.RawReservation;

public interface Reservation extends BaseLoan {

    static Reservation sanitized(final RawReservation original) {
        return Reservation.sanitize(original).build();
    }

    static ReservationBuilder custom() {
        return new MutableReservationImpl();
    }

    static ReservationBuilder sanitize(final RawReservation original) {
        return new MutableReservationImpl(original);
    }

    MyReservation getMyReservation();
}
