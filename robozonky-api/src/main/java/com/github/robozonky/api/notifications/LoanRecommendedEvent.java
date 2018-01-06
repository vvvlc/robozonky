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

package com.github.robozonky.api.notifications;

import java.math.BigDecimal;

import com.github.robozonky.api.remote.entities.Loan;
import com.github.robozonky.api.strategies.InvestmentStrategy;
import com.github.robozonky.api.strategies.RecommendedLoan;

/**
 * Fired immediately after {@link InvestmentStrategy} has recommended a particular loan.
 * {@link InvestmentRequestedEvent} may be fired next.
 */
public final class LoanRecommendedEvent extends Event implements LoanBased,
                                                                 Recommending {

    private final Loan loan;
    private final BigDecimal recommendation;

    public LoanRecommendedEvent(final RecommendedLoan recommendation) {
        this.loan = recommendation.descriptor().item();
        this.recommendation = recommendation.amount();
    }

    /**
     * @return The recommendation to be submitted to the investing algorithm.
     */
    @Override
    public BigDecimal getRecommendation() {
        return recommendation;
    }

    @Override
    public Loan getLoan() {
        return loan;
    }
}
