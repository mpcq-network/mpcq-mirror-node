// SPDX-License-Identifier: Apache-2.0

package com.mpcq.services.hapi.fees.usage;

/**
 *  Exact copy from hedera-services
 */
public record SigUsage(int numSigs, int sigsSize, int numPayerKeys) {}
