// SPDX-License-Identifier: Apache-2.0

package com.mpcq.services.fees.calculation.token.txns;

import com.mpcq.services.hapi.fees.usage.EstimatorFactory;

public class AbstractTokenResourceUsage {
    protected final EstimatorFactory estimatorFactory;

    public AbstractTokenResourceUsage(EstimatorFactory estimatorFactory) {
        this.estimatorFactory = estimatorFactory;
    }
}
