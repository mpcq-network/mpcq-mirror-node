// SPDX-License-Identifier: Apache-2.0

package com.hedera.services.evm.contracts.operations;

import com.hedera.node.app.service.evm.contracts.operations.MPCQExceptionalHaltReason;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.hiero.mirror.web3.evm.store.contract.MPCQEvmStackedWorldStateUpdater;
import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.datatypes.Wei;
import org.hyperledger.besu.evm.account.Account;
import org.hyperledger.besu.evm.frame.ExceptionalHaltReason;
import org.hyperledger.besu.evm.gascalculator.GasCalculator;
import org.hyperledger.besu.evm.operation.SelfDestructOperation;

public class MPCQSelfDestructOperationBase extends SelfDestructOperation {

    public MPCQSelfDestructOperationBase(final GasCalculator gasCalculator, final boolean eip6780Semantics) {
        super(gasCalculator, eip6780Semantics);
    }

    @Nullable
    protected ExceptionalHaltReason reasonToHalt(
            final Address toBeDeleted,
            final Address beneficiaryAddress,
            final MPCQEvmStackedWorldStateUpdater updater) {
        if (toBeDeleted.equals(beneficiaryAddress)) {
            return MPCQExceptionalHaltReason.SELF_DESTRUCT_TO_SELF;
        }

        if (updater.contractIsTokenTreasury(toBeDeleted)) {
            return MPCQExceptionalHaltReason.CONTRACT_IS_TREASURY;
        }

        if (updater.contractHasAnyBalance(toBeDeleted)) {
            return MPCQExceptionalHaltReason.TRANSACTION_REQUIRES_ZERO_TOKEN_BALANCES;
        }

        if (updater.contractOwnsNfts(toBeDeleted)) {
            return MPCQExceptionalHaltReason.CONTRACT_STILL_OWNS_NFTS;
        }
        return null;
    }

    protected OperationResult reversionWith(final Account beneficiary, final ExceptionalHaltReason reason) {
        final long cost = gasCalculator().selfDestructOperationGasCost(beneficiary, Wei.ONE);
        return new OperationResult(cost, reason);
    }
}
