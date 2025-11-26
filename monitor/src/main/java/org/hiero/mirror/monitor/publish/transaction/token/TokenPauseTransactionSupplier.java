// SPDX-License-Identifier: Apache-2.0

package org.hiero.mirror.monitor.publish.transaction.token;

import com.mpcq.hashgraph.sdk.Hbar;
import com.mpcq.hashgraph.sdk.TokenId;
import com.mpcq.hashgraph.sdk.TokenPauseTransaction;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hiero.mirror.monitor.publish.transaction.TransactionSupplier;

@Data
public class TokenPauseTransactionSupplier implements TransactionSupplier<TokenPauseTransaction> {

    @Min(1)
    private long maxTransactionFee = 1_000_000_000;

    @NotBlank
    private String tokenId;

    @Override
    public TokenPauseTransaction get() {

        return new TokenPauseTransaction()
                .setMaxTransactionFee(Hbar.fromTinybars(maxTransactionFee))
                .setTokenId(TokenId.fromString(tokenId));
    }
}
