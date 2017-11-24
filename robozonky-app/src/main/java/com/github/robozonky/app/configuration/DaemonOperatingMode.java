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

package com.github.robozonky.app.configuration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.github.robozonky.app.authentication.Authenticated;
import com.github.robozonky.app.configuration.daemon.DaemonInvestmentMode;
import com.github.robozonky.app.investing.Investor;
import com.github.robozonky.app.portfolio.PortfolioUpdater;
import com.github.robozonky.common.extensions.MarketplaceLoader;
import com.github.robozonky.common.secrets.Credentials;
import com.github.robozonky.util.Scheduler;

@Parameters(commandNames = "daemon", commandDescription = "Constantly checks marketplaces, invests based on strategy.")
class DaemonOperatingMode extends OperatingMode {

    @ParametersDelegate
    MarketplaceCommandLineFragment marketplace = new MarketplaceCommandLineFragment();

    @ParametersDelegate
    StrategyCommandLineFragment strategy = new StrategyCommandLineFragment();

    private static LocalDateTime getNextFourAM(final LocalDateTime now) {
        final LocalDateTime fourAM = LocalTime.of(4, 0).atDate(now.toLocalDate());
        if (fourAM.isAfter(now)) {
            return fourAM;
        }
        return fourAM.plusDays(1);
    }

    private static Duration timeUntil4am(final LocalDateTime now) {
        final LocalDateTime nextFourAm = getNextFourAM(now);
        return Duration.between(now, nextFourAm);
    }

    @Override
    protected Optional<InvestmentMode> getInvestmentMode(final CommandLine cli, final Authenticated auth,
                                                         final Investor.Builder builder) {
        final boolean isFaultTolerant = cli.getTweaksFragment().isFaultTolerant();
        final Credentials cred = new Credentials(marketplace.getMarketplaceCredentials(),
                                                 auth.getSecretProvider());
        return MarketplaceLoader.load(cred)
                .map(marketplaceImpl -> {
                    final InvestmentMode m = new DaemonInvestmentMode(auth, builder, isFaultTolerant, marketplaceImpl,
                                                                      strategy.getStrategyLocation(),
                                                                      marketplace.getMaximumSleepDuration(),
                                                                      marketplace.getPrimaryMarketplaceCheckDelay(),
                                                                      marketplace.getSecondaryMarketplaceCheckDelay());
                    // only schedule internal data updates after daemon had a chance to initialize
                    final PortfolioUpdater updater = new PortfolioUpdater(auth);
                    // schedule the first run immediately...
                    Scheduler.inBackground().run(updater);
                    // ... and then run every 12 hours at 4 am
                    Scheduler.inBackground().submit(updater, Duration.ofHours(12), timeUntil4am(LocalDateTime.now()));
                    return Optional.of(m);
                }).orElse(Optional.empty());
    }
}
