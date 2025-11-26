// SPDX-License-Identifier: Apache-2.0

package com.mpcq.services.store.contracts.precompile.codec;

import com.hederahashgraph.api.proto.java.TokenID;

public record PauseWrapper(TokenID token) {}
