// SPDX-License-Identifier: Apache-2.0

package org.hiero.mirror.web3.evm.contracts.execution;

import com.hedera.node.app.service.evm.contracts.execution.MPCQEvmTransactionProcessingResult;
import org.hiero.mirror.web3.service.model.CallServiceParameters;

public interface MirrorEvmTxProcessor {

    MPCQEvmTransactionProcessingResult execute(final CallServiceParameters params, final long estimatedGas);
}
