// SPDX-License-Identifier: Apache-2.0

package com.mpcq.services.hapi.fees.usage;

import com.hederahashgraph.api.proto.java.TransactionBody;

/**
 *  Exact copy from hedera-services
 */
@FunctionalInterface
public interface EstimatorFactory {
    TxnUsageEstimator get(SigUsage sigUsage, TransactionBody txn, EstimatorUtils utils);
}
