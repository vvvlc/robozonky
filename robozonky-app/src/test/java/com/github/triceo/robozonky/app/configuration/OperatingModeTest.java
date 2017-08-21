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

package com.github.triceo.robozonky.app.configuration;

import java.util.Optional;
import java.util.UUID;

import com.github.triceo.robozonky.api.ReturnCode;
import com.github.triceo.robozonky.api.confirmations.ConfirmationProvider;
import com.github.triceo.robozonky.app.authentication.Authenticated;
import com.github.triceo.robozonky.app.investing.Investor;
import com.github.triceo.robozonky.common.secrets.SecretProvider;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.mockito.Mockito;

public class OperatingModeTest {

    @Test
    public void defaultTesting() {
        final CommandLine cli = Mockito.mock(CommandLine.class);
        Mockito.when(cli.getTweaksFragment()).thenReturn(Mockito.mock(TweaksCommandLineFragment.class));
        final Investor.Builder builder = new Investor.Builder();
        builder.usingConfirmation(Mockito.mock(ConfirmationProvider.class), new char[0]);
        final Authenticated auth =
                Authenticated.passwordBased(SecretProvider.fallback("user", "pass".toCharArray()));
        final OperatingMode mode = new TestOperatingMode();
        final Optional<InvestmentMode> config = mode.getInvestmentMode(cli, auth, builder);
        Assertions.assertThat(config).isPresent();
        final InvestmentMode result = config.get();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.isFaultTolerant()).isFalse();
            softly.assertThat(result.get()).isEqualTo(ReturnCode.OK);
        });
    }

    @Test
    public void withConfirmation() {
        final TweaksCommandLineFragment f = Mockito.mock(TweaksCommandLineFragment.class);
        Mockito.when(f.isDryRunEnabled()).thenReturn(true);
        final CommandLine cli = Mockito.mock(CommandLine.class);
        Mockito.when(cli.getTweaksFragment()).thenReturn(f);
        final Authenticated auth =
                Authenticated.passwordBased(SecretProvider.fallback("user", "pass".toCharArray()));
        final ConfirmationCommandLineFragment fragment = new ConfirmationCommandLineFragment();
        fragment.confirmationCredentials = "zonkoid:123456";
        Mockito.when(cli.getConfirmationFragment()).thenReturn(fragment);
        final OperatingMode mode = new DaemonOperatingMode();
        final Optional<InvestmentMode> config = mode.configure(cli, auth);
        Assertions.assertThat(config).isPresent();
    }

    @Test
    public void withConfirmationAndUnknownId() {
        final CommandLine cli = Mockito.mock(CommandLine.class);
        Mockito.when(cli.getTweaksFragment()).thenReturn(Mockito.mock(TweaksCommandLineFragment.class));
        final Authenticated auth =
                Authenticated.passwordBased(SecretProvider.fallback("user", "pass".toCharArray()));
        final ConfirmationCommandLineFragment fragment = new ConfirmationCommandLineFragment();
        fragment.confirmationCredentials = UUID.randomUUID().toString();
        Mockito.when(cli.getConfirmationFragment()).thenReturn(fragment);
        final OperatingMode mode = new DaemonOperatingMode();
        final Optional<InvestmentMode> config = mode.configure(cli, auth);
        Assertions.assertThat(config).isEmpty();
    }

    @Test
    public void withConfirmationAndNoSecret() {
        final CommandLine cli = Mockito.mock(CommandLine.class);
        Mockito.when(cli.getTweaksFragment()).thenReturn(Mockito.mock(TweaksCommandLineFragment.class));
        final Authenticated auth =
                Authenticated.passwordBased(SecretProvider.fallback("user", "pass".toCharArray()));
        final ConfirmationCommandLineFragment fragment = new ConfirmationCommandLineFragment();
        fragment.confirmationCredentials = "zonkoid";
        Mockito.when(cli.getConfirmationFragment()).thenReturn(fragment);
        final OperatingMode mode = new DaemonOperatingMode();
        final Optional<InvestmentMode> config = mode.configure(cli, auth);
        Assertions.assertThat(config).isEmpty();
    }

    @Test
    public void withoutConfirmation() {
        final CommandLine cli = Mockito.mock(CommandLine.class);
        Mockito.when(cli.getTweaksFragment()).thenReturn(Mockito.mock(TweaksCommandLineFragment.class));
        Mockito.when(cli.getConfirmationFragment()).thenReturn(Mockito.mock(ConfirmationCommandLineFragment.class));
        final Authenticated auth = Mockito.mock(Authenticated.class);
        Mockito.when(auth.getSecretProvider()).thenReturn(SecretProvider.fallback("user", new char[0]));
        final OperatingMode mode = new DaemonOperatingMode();
        final Optional<InvestmentMode> config = mode.configure(cli, auth);
        Assertions.assertThat(config).isPresent();
    }
}
