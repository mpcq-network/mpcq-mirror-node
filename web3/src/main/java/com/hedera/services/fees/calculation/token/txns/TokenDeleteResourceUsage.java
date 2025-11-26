// SPDX-License-Identifier: Apache-2.0

package com.mpcq.services.fees.calculation.token.txns;

import static com.mpcq.services.hapi.fees.usage.SingletonEstimatorUtils.ESTIMATOR_UTILS;

import com.mpcq.services.fees.calculation.TxnResourceUsageEstimator;
import com.mpcq.services.fees.usage.token.TokenDeleteUsage;
import com.mpcq.services.hapi.fees.usage.EstimatorFactory;
import com.mpcq.services.hapi.fees.usage.SigUsage;
import com.mpcq.services.hapi.fees.usage.TxnUsageEstimator;
import com.mpcq.services.hapi.utils.fees.SigValueObj;
import com.hederahashgraph.api.proto.java.FeeData;
import com.hederahashgraph.api.proto.java.TransactionBody;
import java.util.function.BiFunction;

/**
 * Exact copy from hedera-services
 */
public class TokenDeleteResourceUsage extends AbstractTokenResourceUsage implements TxnResourceUsageEstimator {
    private static final BiFunction<TransactionBody, TxnUsageEstimator, TokenDeleteUsage> factory =
            TokenDeleteUsage::newEstimate;

    public TokenDeleteResourceUsage(final EstimatorFactory estimatorFactory) {
        super(estimatorFactory);
    }

    @Override
    public boolean applicableTo(final TransactionBody txn) {
        return txn.hasTokenDeletion();
    }

    @Override
    public FeeData usageGiven(TransactionBody txn, SigValueObj svo) {
        final var sigUsage = new SigUsage(svo.getTotalSigCount(), svo.getSignatureSize(), svo.getPayerAcctSigCount());
        final var estimate = factory.apply(txn, estimatorFactory.get(sigUsage, txn, ESTIMATOR_UTILS));
        return estimate.get();
    }
}
