// SPDX-License-Identifier: Apache-2.0

package com.mpcq.services.store.contracts.precompile.codec;

import com.mpcq.node.app.service.evm.store.tokens.TokenAccessor;
import com.hederahashgraph.api.proto.java.TokenID;
import java.util.function.Predicate;
import org.hyperledger.besu.datatypes.Address;

public record ERCTransferParams(
        int functionId, Address senderAddress, TokenAccessor tokenAccessor, TokenID tokenID, Predicate<Address> exists)
        implements BodyParams {}
