// SPDX-License-Identifier: Apache-2.0

package com.mpcq.services.store.contracts.precompile;

import java.util.List;

public record CryptoTransferWrapper(
        TransferWrapper transferWrapper, List<TokenTransferWrapper> tokenTransferWrappers) {}
