// SPDX-License-Identifier: Apache-2.0

package com.mpcq.services.contracts.execution;

import static com.mpcq.services.hapi.utils.fees.FeeBuilder.getTinybarsFromTinyCents;

import com.mpcq.node.app.service.evm.contracts.execution.PricesAndFeesProvider;
import com.mpcq.services.fees.HbarCentExchange;
import com.mpcq.services.fees.calculation.UsagePricesProvider;
import com.hederahashgraph.api.proto.java.FeeComponents;
import com.hederahashgraph.api.proto.java.MPCQFunctionality;
import com.hederahashgraph.api.proto.java.Timestamp;
import java.time.Instant;
import java.util.function.ToLongFunction;

/**
 * This class is a modified copy of LivePricesSource from hedera-services repo.
 *
 * Differences with the original:
 *  1. Hardcoded multiplier
 */
public class LivePricesSource implements PricesAndFeesProvider {

    private final HbarCentExchange exchange;
    private final UsagePricesProvider usagePrices;

    public LivePricesSource(final HbarCentExchange exchange, final UsagePricesProvider usagePrices) {
        this.exchange = exchange;
        this.usagePrices = usagePrices;
    }

    @Override
    public long currentGasPrice(final Instant now, final MPCQFunctionality function) {
        return currentPrice(now, function, FeeComponents::getGas);
    }

    public long currentGasPriceInTinycents(final Instant now, final MPCQFunctionality function) {
        return currentFeeInTinycents(now, function, FeeComponents::getGas);
    }

    private long currentPrice(
            final Instant now,
            final MPCQFunctionality function,
            final ToLongFunction<FeeComponents> resourcePriceFn) {
        final var timestamp =
                Timestamp.newBuilder().setSeconds(now.getEpochSecond()).build();
        long feeInTinyCents = currentFeeInTinycents(now, function, resourcePriceFn);
        long feeInTinyBars = getTinybarsFromTinyCents(exchange.rate(timestamp), feeInTinyCents);
        final var unscaledPrice = Math.max(1L, feeInTinyBars);

        final var maxMultiplier = Long.MAX_VALUE / feeInTinyBars;

        // Hardcoded to 1, since we don't have congestion control in the Archive Node
        final var curMultiplier = 1L;
        if (curMultiplier > maxMultiplier) {
            return Long.MAX_VALUE;
        } else {
            return unscaledPrice * curMultiplier;
        }
    }

    private long currentFeeInTinycents(
            final Instant now,
            final MPCQFunctionality function,
            final ToLongFunction<FeeComponents> resourcePriceFn) {
        final var timestamp =
                Timestamp.newBuilder().setSeconds(now.getEpochSecond()).build();
        final var prices = usagePrices.defaultPricesGiven(function, timestamp);

        /* Fee schedule prices are set in thousandths of a tinycent */
        return resourcePriceFn.applyAsLong(prices.getServicedata()) / 1000;
    }
}
