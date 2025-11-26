// SPDX-License-Identifier: Apache-2.0

package com.mpcq.services.store.contracts.precompile.codec;

import com.mpcq.services.store.models.Id;
import org.hyperledger.besu.datatypes.Address;

/**
 * Record containing specific body arguments for Approve precompiles.
 * */
public record ApproveParams(Address tokenAddress, Address senderAddress, Id ownerId, boolean isFungible)
        implements BodyParams {}
