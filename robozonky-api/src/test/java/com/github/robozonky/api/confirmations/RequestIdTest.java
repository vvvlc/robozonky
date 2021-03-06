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

import java.util.UUID;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RequestIdTest {

    @Test
    void nullUsername() {
        assertThatThrownBy(() -> new RequestId(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void emptyPassword() {
        final String username = "user";
        final RequestId r = new RequestId(username);
        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(r.getUserId()).isSameAs(username);
        softly.assertThat(r.getPassword()).isEmpty();
        softly.assertAll();
    }

    @Test
    void passwordDefensivelyCopied() {
        final char[] password = "pass".toCharArray();
        final RequestId r = new RequestId("user", password);
        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(r.getPassword())
                .containsExactly(password)
                .isNotSameAs(password);
        softly.assertAll();
    }

    @Test
    void equalsSame() {
        final char[] password = "pass".toCharArray();
        final String username = "user";
        final RequestId r1 = new RequestId(username, password);
        assertThat(r1).isEqualTo(r1);
        final RequestId r2 = new RequestId(username, password);
        assertThat(r1).isEqualTo(r2);
    }

    @Test
    void notEqualsPassword() {
        final String username = "user";
        final RequestId r1 = new RequestId(username, UUID.randomUUID().toString().toCharArray());
        final RequestId r2 = new RequestId(username, UUID.randomUUID().toString().toCharArray());
        assertThat(r1).isNotEqualTo(r2);
    }

    @Test
    void notEqualsUsername() {
        final char[] password = "password".toCharArray();
        final RequestId r1 = new RequestId(UUID.randomUUID().toString(), password);
        final RequestId r2 = new RequestId(UUID.randomUUID().toString(), password);
        assertThat(r1).isNotEqualTo(r2);
    }

    @Test
    void notEqualsDifferentJavaType() {
        final RequestId r1 = new RequestId(UUID.randomUUID().toString(), UUID.randomUUID().toString().toCharArray());
        assertThat(r1).isNotEqualTo(r1.toString());
    }
}
