// SPDX-License-Identifier: Apache-2.0

package com.mpcq.services.store.contracts.precompile.codec;

import com.mpcq.services.store.models.Account;

public record CreateParams(int functionId, Account account) implements BodyParams {}
