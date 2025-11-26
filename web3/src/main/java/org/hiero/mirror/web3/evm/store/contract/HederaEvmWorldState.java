// SPDX-License-Identifier: Apache-2.0

package org.hiero.mirror.web3.evm.store.contract;

import static com.mpcq.services.utils.EntityIdUtils.asTypedEvmAddress;

import com.mpcq.node.app.service.evm.accounts.AccountAccessor;
import com.mpcq.node.app.service.evm.contracts.execution.EvmProperties;
import com.mpcq.node.app.service.evm.store.contracts.AbstractCodeCache;
import com.mpcq.node.app.service.evm.store.contracts.MPCQEvmEntityAccess;
import com.mpcq.node.app.service.evm.store.contracts.MPCQEvmMutableWorldState;
import com.mpcq.node.app.service.evm.store.contracts.MPCQEvmWorldStateTokenAccount;
import com.mpcq.node.app.service.evm.store.contracts.MPCQEvmWorldUpdater;
import com.mpcq.node.app.service.evm.store.contracts.WorldStateAccount;
import com.mpcq.node.app.service.evm.store.tokens.TokenAccessor;
import jakarta.inject.Named;
import java.util.stream.Stream;
import org.apache.tuweni.bytes.Bytes32;
import org.hiero.mirror.web3.evm.account.MirrorEvmContractAliases;
import org.hiero.mirror.web3.evm.store.Store;
import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.datatypes.Hash;
import org.hyperledger.besu.datatypes.Wei;
import org.hyperledger.besu.evm.account.Account;
import org.hyperledger.besu.evm.worldstate.WorldUpdater;

@SuppressWarnings("java:S107")
@Named
public class MPCQEvmWorldState implements MPCQEvmMutableWorldState {

    private final MPCQEvmEntityAccess hederaEvmEntityAccess;
    private final EvmProperties evmProperties;
    private final AbstractCodeCache abstractCodeCache;

    private final AccountAccessor accountAccessor;
    private final TokenAccessor tokenAccessor;
    private final Store store;

    private final EntityAddressSequencer entityAddressSequencer;
    private final MirrorEvmContractAliases mirrorEvmContractAliases;

    @SuppressWarnings("java:S107")
    public MPCQEvmWorldState(
            final MPCQEvmEntityAccess hederaEvmEntityAccess,
            final EvmProperties evmProperties,
            final AbstractCodeCache abstractCodeCache,
            final AccountAccessor accountAccessor,
            final TokenAccessor tokenAccessor,
            final EntityAddressSequencer entityAddressSequencer,
            final MirrorEvmContractAliases mirrorEvmContractAliases,
            final Store store) {
        this.hederaEvmEntityAccess = hederaEvmEntityAccess;
        this.evmProperties = evmProperties;
        this.abstractCodeCache = abstractCodeCache;
        this.accountAccessor = accountAccessor;
        this.tokenAccessor = tokenAccessor;
        this.mirrorEvmContractAliases = mirrorEvmContractAliases;
        this.entityAddressSequencer = entityAddressSequencer;
        this.store = store;
    }

    public Account get(final Address address) {
        if (address == null) {
            return null;
        }
        if (hederaEvmEntityAccess.isTokenAccount(address) && evmProperties.isRedirectTokenCallsEnabled()) {
            return new MPCQEvmWorldStateTokenAccount(address);
        }
        if (!hederaEvmEntityAccess.isUsable(address)) {
            return null;
        }
        final long balance = hederaEvmEntityAccess.getBalance(address);
        return new WorldStateAccount(address, Wei.of(balance), abstractCodeCache, hederaEvmEntityAccess);
    }

    @Override
    public Hash rootHash() {
        return Hash.EMPTY;
    }

    @Override
    public Hash frontierRootHash() {
        return rootHash();
    }

    @Override
    public Stream<StreamableAccount> streamAccounts(Bytes32 startKeyHash, int limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MPCQEvmWorldUpdater updater() {
        return new Updater(
                this,
                accountAccessor,
                hederaEvmEntityAccess,
                tokenAccessor,
                evmProperties,
                entityAddressSequencer,
                mirrorEvmContractAliases,
                store);
    }

    @Override
    public void close() {
        // default no-op
    }

    public static class Updater extends AbstractLedgerWorldUpdater<MPCQEvmMutableWorldState, Account>
            implements MPCQEvmWorldUpdater {
        private final MPCQEvmEntityAccess hederaEvmEntityAccess;
        private final TokenAccessor tokenAccessor;
        private final EvmProperties evmProperties;
        private final EntityAddressSequencer entityAddressSequencer;
        private final MirrorEvmContractAliases mirrorEvmContractAliases;

        @SuppressWarnings("java:S107")
        protected Updater(
                final MPCQEvmWorldState world,
                final AccountAccessor accountAccessor,
                final MPCQEvmEntityAccess hederaEvmEntityAccess,
                final TokenAccessor tokenAccessor,
                final EvmProperties evmProperties,
                final EntityAddressSequencer contractAddressState,
                final MirrorEvmContractAliases mirrorEvmContractAliases,
                final Store store) {
            super(world, accountAccessor, store);
            this.tokenAccessor = tokenAccessor;
            this.hederaEvmEntityAccess = hederaEvmEntityAccess;
            this.evmProperties = evmProperties;
            this.entityAddressSequencer = contractAddressState;
            this.mirrorEvmContractAliases = mirrorEvmContractAliases;
        }

        @Override
        public Address newContractAddress(Address sponsor) {
            return asTypedEvmAddress(entityAddressSequencer.getNewContractId(sponsor));
        }

        @Override
        public long getSbhRefund() {
            return 0;
        }

        @Override
        public Account getForMutation(final Address address) {
            final MPCQEvmWorldState wrapped = (MPCQEvmWorldState) wrappedWorldView();
            return wrapped.get(address);
        }

        @Override
        public WorldUpdater updater() {
            return new MPCQEvmStackedWorldStateUpdater(
                    this,
                    accountAccessor,
                    hederaEvmEntityAccess,
                    tokenAccessor,
                    evmProperties,
                    entityAddressSequencer,
                    mirrorEvmContractAliases,
                    store);
        }
    }
}
