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

package com.github.robozonky.notifications.listeners;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;

import com.github.robozonky.api.SessionInfo;
import com.github.robozonky.api.notifications.EventListener;
import com.github.robozonky.api.notifications.LoanDelinquent10DaysOrMoreEvent;
import com.github.robozonky.api.notifications.LoanDelinquentEvent;
import com.github.robozonky.api.notifications.LoanNoLongerDelinquentEvent;
import com.github.robozonky.api.remote.entities.sanitized.Investment;
import com.github.robozonky.api.remote.entities.sanitized.Loan;
import com.github.robozonky.api.remote.enums.MainIncomeType;
import com.github.robozonky.api.remote.enums.Purpose;
import com.github.robozonky.api.remote.enums.Rating;
import com.github.robozonky.api.remote.enums.Region;
import com.github.robozonky.notifications.AbstractTargetHandler;
import com.github.robozonky.notifications.SupportedListener;
import com.github.robozonky.notifications.Target;
import com.github.robozonky.test.AbstractRoboZonkyTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class DelinquencyTrackerTest extends AbstractRoboZonkyTest {

    public static URL getSomeUrl() {
        try {
            return new URL("http://localhost");
        } catch (final MalformedURLException ex) {
            Assertions.fail("Shouldn't have happened.", ex);
            return null;
        }
    }

    private static final Loan LOAN = Loan.custom()
            .setId(1)
            .setAmount(200)
            .setRating(Rating.D)
            .setPurpose(Purpose.AUTO_MOTO)
            .setRegion(Region.JIHOCESKY)
            .setMainIncomeType(MainIncomeType.EMPLOYMENT)
            .setName("")
            .setUrl(getSomeUrl())
            .build();
    private static final Investment INVESTMENT = Investment.fresh(LOAN, 200).build();
    private static SessionInfo SESSION = new SessionInfo("someone@robozonky.cz");

    @Test
    void standard() {
        final DelinquencyTracker t = new DelinquencyTracker(Target.EMAIL);
        assertThat(t.isDelinquent(SESSION, INVESTMENT)).isFalse();
        t.setDelinquent(SESSION, INVESTMENT);
        assertThat(t.isDelinquent(SESSION, INVESTMENT)).isTrue();
        t.unsetDelinquent(SESSION, INVESTMENT);
        assertThat(t.isDelinquent(SESSION, INVESTMENT)).isFalse();
    }

    @Test
    void notifying() throws Exception {
        final AbstractTargetHandler h = AbstractListenerTest.getHandler();
        final EventListener<LoanDelinquentEvent> l =
                new LoanDelinquentEventListener(SupportedListener.LOAN_DELINQUENT_10_PLUS, h);
        final EventListener<LoanNoLongerDelinquentEvent> l2 =
                new LoanNoLongerDelinquentEventListener(SupportedListener.LOAN_NO_LONGER_DELINQUENT, h);
        final LoanNoLongerDelinquentEvent evt = new LoanNoLongerDelinquentEvent(INVESTMENT, LOAN);
        l2.handle(evt, SESSION);
        verify(h, never()).actuallySend(any(), any(), any()); // not delinquent before, not sending
        l.handle(new LoanDelinquent10DaysOrMoreEvent(INVESTMENT, LOAN, LocalDate.now(), Collections.emptyList()),
                 SESSION);
        verify(h).actuallySend(eq(SESSION), any(), any());
        l2.handle(evt, SESSION);
        verify(h, times(2)).actuallySend(eq(SESSION), any(), any()); // delinquency now registered, send
        l2.handle(evt, SESSION);
        verify(h, times(2)).actuallySend(eq(SESSION), any(), any()); // already unregistered, no send
    }

}