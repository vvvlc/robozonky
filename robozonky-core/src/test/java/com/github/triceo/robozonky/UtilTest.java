/*
 * Copyright 2016 Lukáš Petrovický
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

package com.github.triceo.robozonky;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.triceo.robozonky.remote.Loan;
import com.github.triceo.robozonky.remote.Investment;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

public class UtilTest {

    private static Investment getMockInvestmentWithId(final int id) {
        final Investment i = Mockito.mock(Investment.class);
        Mockito.when(i.getLoanId()).thenReturn(id);
        return i;
    }

    @Test
    public void properLoanSorting() {
        // mock
        final Loan shortLoan = Mockito.mock(Loan.class);
        Mockito.when(shortLoan.getTermInMonths()).thenReturn(10);
        final Loan mediumLoan = Mockito.mock(Loan.class);
        Mockito.when(mediumLoan.getTermInMonths()).thenReturn(20);
        final Loan longLoan = Mockito.mock(Loan.class);
        Mockito.when(longLoan.getTermInMonths()).thenReturn(30);
        // test
        List<Loan> result = Util.sortLoansByTerm(Arrays.asList(longLoan, mediumLoan, shortLoan, longLoan), true);
        Assertions.assertThat(result).hasSize(3);
        Assertions.assertThat(result).containsSequence(longLoan, mediumLoan, shortLoan);
        result = Util.sortLoansByTerm(Arrays.asList(longLoan, mediumLoan, shortLoan, longLoan), false);
        Assertions.assertThat(result).hasSize(3);
        Assertions.assertThat(result).containsSequence(shortLoan, mediumLoan, longLoan);
    }

    @Test
    public void mergingTwoInvestmentCollectinsWorksProperly() {
        final Investment I1 = UtilTest.getMockInvestmentWithId(1);
        final Investment I2 = UtilTest.getMockInvestmentWithId(2);
        final Investment I3 = UtilTest.getMockInvestmentWithId(3);

        // two identical investments will result in one
        final List<Investment> a = Arrays.asList(I1, I2);
        final List<Investment> b = Arrays.asList(I2, I3);
        Assertions.assertThat(Util.mergeInvestments(a, b)).containsExactly(I1, I2, I3);

        // standard merging also works
        final List<Investment> c = Collections.singletonList(I3);
        Assertions.assertThat(Util.mergeInvestments(a, c)).containsExactly(I1, I2, I3);

        // reverse-order merging works
        final List<Investment> d = Arrays.asList(I2, I1);
        Assertions.assertThat(Util.mergeInvestments(a, d)).containsExactly(I1, I2);

        // two non-identical loans with same ID are merged in the order in which they came
        final Investment I3_2 = UtilTest.getMockInvestmentWithId(3);
        final List<Investment> e = Collections.singletonList(I3_2);
        Assertions.assertThat(Util.mergeInvestments(c, e)).containsExactly(I3);
        Assertions.assertThat(Util.mergeInvestments(e, c)).containsExactly(I3_2);
    }

}
