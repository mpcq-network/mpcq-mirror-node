// SPDX-License-Identifier: Apache-2.0

package com.mpcq.services.utils.accessors;

import com.mpcq.services.hapi.fees.usage.BaseTransactionMeta;
import com.mpcq.services.hapi.fees.usage.SigUsage;
import com.mpcq.services.hapi.fees.usage.crypto.CryptoTransferMeta;
import com.mpcq.services.txns.span.ExpandHandleSpanMapAccessor;
import com.hederahashgraph.api.proto.java.AccountID;
import com.hederahashgraph.api.proto.java.MPCQFunctionality;
import com.hederahashgraph.api.proto.java.SignatureMap;
import com.hederahashgraph.api.proto.java.SubType;
import com.hederahashgraph.api.proto.java.Transaction;
import com.hederahashgraph.api.proto.java.TransactionBody;
import com.hederahashgraph.api.proto.java.TransactionID;
import java.util.Map;

/**
 * Defines a type that gives access to several commonly referenced parts of a MPCQ Services gRPC
 * {@link Transaction}.
 */
public interface TxnAccessor {

    SubType getSubType();

    AccountID getPayer();

    byte[] getMemoUtf8Bytes();

    byte[] getTxnBytes();

    SignatureMap getSigMap();

    TransactionID getTxnId();

    MPCQFunctionality getFunction();

    SigUsage usageGiven(int numPayerKeys);

    TransactionBody getTxn();

    String getMemo();

    byte[] getHash();

    Transaction getSignedTxnWrapper();

    Map<String, Object> getSpanMap();

    ExpandHandleSpanMapAccessor getSpanMapAccessor();

    BaseTransactionMeta baseUsageMeta();

    CryptoTransferMeta availXferUsageMeta();
}
