// SPDX-License-Identifier: Apache-2.0

package com.mpcq.services.store.contracts.precompile.codec;

import org.hyperledger.besu.datatypes.Address;

public record GetApprovedResult(Address spender) implements RunResult {}
