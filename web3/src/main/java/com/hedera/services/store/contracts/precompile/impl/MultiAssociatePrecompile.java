// SPDX-License-Identifier: Apache-2.0

package com.mpcq.services.store.contracts.precompile.impl;

import static com.mpcq.node.app.service.evm.store.contracts.precompile.codec.EvmDecodingFacade.decodeFunctionCall;
import static com.mpcq.services.hapi.utils.contracts.ParsingConstants.INT;
import static com.mpcq.services.store.contracts.precompile.AbiConstants.ABI_ID_ASSOCIATE_TOKENS;
import static com.mpcq.services.store.contracts.precompile.codec.DecodingFacade.convertLeftPaddedAddressToAccountId;
import static com.mpcq.services.store.contracts.precompile.codec.DecodingFacade.decodeTokenIDsFromBytesArray;

import com.esaulpaugh.headlong.abi.ABIType;
import com.esaulpaugh.headlong.abi.Function;
import com.esaulpaugh.headlong.abi.Tuple;
import com.esaulpaugh.headlong.abi.TypeFactory;
import com.mpcq.services.store.contracts.precompile.SyntheticTxnFactory;
import com.mpcq.services.store.contracts.precompile.codec.Association;
import com.mpcq.services.store.contracts.precompile.codec.BodyParams;
import com.mpcq.services.store.contracts.precompile.utils.PrecompilePricingUtils;
import com.mpcq.services.txn.token.AssociateLogic;
import com.hederahashgraph.api.proto.java.AccountID;
import com.hederahashgraph.api.proto.java.TransactionBody;
import java.util.Set;
import java.util.function.UnaryOperator;
import org.apache.tuweni.bytes.Bytes;

public class MultiAssociatePrecompile extends AbstractAssociatePrecompile {

    private static final Function ASSOCIATE_TOKENS_FUNCTION = new Function("associateTokens(address,address[])", INT);
    private static final Bytes ASSOCIATE_TOKENS_SELECTOR = Bytes.wrap(ASSOCIATE_TOKENS_FUNCTION.selector());
    private static final ABIType<Tuple> ASSOCIATE_TOKENS_DECODER = TypeFactory.create("(bytes32,bytes32[])");

    public MultiAssociatePrecompile(
            final PrecompilePricingUtils pricingUtils,
            final SyntheticTxnFactory syntheticTxnFactory,
            final AssociateLogic associateLogic) {
        super(pricingUtils, syntheticTxnFactory, associateLogic);
    }

    public static Association decodeMultipleAssociations(final Bytes input, final UnaryOperator<byte[]> aliasResolver) {
        final Tuple decodedArguments = decodeFunctionCall(input, ASSOCIATE_TOKENS_SELECTOR, ASSOCIATE_TOKENS_DECODER);

        final var accountID = convertLeftPaddedAddressToAccountId(decodedArguments.get(0), aliasResolver);
        final var tokenIDs = decodeTokenIDsFromBytesArray(decodedArguments.get(1));

        return Association.multiAssociation(accountID, tokenIDs);
    }

    @Override
    public TransactionBody.Builder body(
            final Bytes input, final UnaryOperator<byte[]> aliasResolver, final BodyParams bodyParams) {
        final Tuple decodedArguments = decodeFunctionCall(input, ASSOCIATE_TOKENS_SELECTOR, ASSOCIATE_TOKENS_DECODER);

        final var accountID = convertLeftPaddedAddressToAccountId(decodedArguments.get(0), aliasResolver);
        final var tokenIDs = decodeTokenIDsFromBytesArray(decodedArguments.get(1));

        final var associateOp = Association.multiAssociation(accountID, tokenIDs);

        return syntheticTxnFactory.createAssociate(associateOp);
    }

    @Override
    public long getGasRequirement(
            final long blockTimestamp, final TransactionBody.Builder transactionBody, final AccountID sender) {
        return pricingUtils.computeGasRequirement(blockTimestamp, this, transactionBody, sender);
    }

    @Override
    public Set<Integer> getFunctionSelectors() {
        return Set.of(ABI_ID_ASSOCIATE_TOKENS);
    }
}
