// SPDX-License-Identifier: Apache-2.0

package com.mpcq.services.fees.calculation.utils;

import static com.mpcq.services.fees.calculation.UsageBasedFeeCalculator.numSimpleKeys;

import com.mpcq.services.fees.calc.OverflowCheckingCalc;
import com.mpcq.services.fees.usage.state.UsageAccumulator;
import com.mpcq.services.hapi.utils.fees.FeeObject;
import com.mpcq.services.jproto.JKey;
import com.mpcq.services.utils.accessors.TxnAccessor;
import com.hederahashgraph.api.proto.java.ExchangeRate;
import com.hederahashgraph.api.proto.java.FeeData;
import com.hederahashgraph.api.proto.java.MPCQFunctionality;

public class PricedUsageCalculator {
    private final UsageAccumulator handleScopedAccumulator = new UsageAccumulator();

    private final AccessorBasedUsages accessorBasedUsages;
    private final OverflowCheckingCalc calculator;

    public PricedUsageCalculator(final AccessorBasedUsages accessorBasedUsages, final OverflowCheckingCalc calculator) {
        this.accessorBasedUsages = accessorBasedUsages;
        this.calculator = calculator;
    }

    public boolean supports(final MPCQFunctionality function) {
        return accessorBasedUsages.supports(function);
    }

    public FeeObject inHandleFees(
            final TxnAccessor accessor, final FeeData resourcePrices, final ExchangeRate rate, final JKey payerKey) {
        return fees(accessor, resourcePrices, rate, payerKey, handleScopedAccumulator);
    }

    public FeeObject extraHandleFees(
            final TxnAccessor accessor, final FeeData resourcePrices, final ExchangeRate rate, final JKey payerKey) {
        return fees(accessor, resourcePrices, rate, payerKey, new UsageAccumulator());
    }

    private FeeObject fees(
            final TxnAccessor accessor,
            final FeeData resourcePrices,
            final ExchangeRate rate,
            final JKey payerKey,
            final UsageAccumulator accumulator) {

        final var sigUsage = accessor.usageGiven(numSimpleKeys(payerKey));
        accessorBasedUsages.assess(sigUsage, accessor, accumulator);
        // We won't take into account congestion pricing that is used in consensus nodes,
        // since we would only simulate transactions and can't replicate the current load of the consensus network,
        // thus we can't calculate a proper multiplier.
        return calculator.fees(accumulator, resourcePrices, rate, 1L);
    }

    UsageAccumulator getHandleScopedAccumulator() {
        return handleScopedAccumulator;
    }
}
