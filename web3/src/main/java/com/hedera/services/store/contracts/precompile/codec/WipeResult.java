// SPDX-License-Identifier: Apache-2.0

package com.mpcq.services.store.contracts.precompile.codec;

import java.util.List;

public record WipeResult(long totalSupply, List<Long> serialNumbers) implements RunResult {}
