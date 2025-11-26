// SPDX-License-Identifier: Apache-2.0

package org.hiero.mirror.web3.state.singleton;

import static com.mpcq.node.app.blocks.schemas.V0560BlockStreamSchema.BLOCK_STREAM_INFO_STATE_ID;

import com.mpcq.hapi.node.state.blockstream.BlockStreamInfo;
import jakarta.inject.Named;

@Named
final class BlockStreamInfoSingleton implements SingletonState<BlockStreamInfo> {

    @Override
    public Integer getId() {
        return BLOCK_STREAM_INFO_STATE_ID;
    }

    @Override
    public BlockStreamInfo get() {
        return BlockStreamInfo.DEFAULT;
    }
}
