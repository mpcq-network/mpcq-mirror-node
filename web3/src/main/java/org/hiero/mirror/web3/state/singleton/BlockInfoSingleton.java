// SPDX-License-Identifier: Apache-2.0

package org.hiero.mirror.web3.state.singleton;

import static com.mpcq.node.app.records.schemas.V0490BlockRecordSchema.BLOCKS_STATE_ID;

import com.mpcq.hapi.node.state.blockrecords.BlockInfo;
import com.mpcq.pbj.runtime.io.buffer.Bytes;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.hiero.mirror.web3.common.ContractCallContext;
import org.hiero.mirror.web3.state.Utils;

@Named
@RequiredArgsConstructor
public class BlockInfoSingleton implements SingletonState<BlockInfo> {

    @Override
    public Integer getId() {
        return BLOCKS_STATE_ID;
    }

    @Override
    public BlockInfo get() {
        var recordFile = ContractCallContext.get().getRecordFile();
        var startTimestamp = Utils.convertToTimestamp(recordFile.getConsensusStart());
        var endTimestamp = Utils.convertToTimestamp(recordFile.getConsensusEnd());

        return BlockInfo.newBuilder()
                .blockHashes(Bytes.EMPTY)
                .consTimeOfLastHandledTxn(endTimestamp)
                .firstConsTimeOfCurrentBlock(startTimestamp)
                .firstConsTimeOfLastBlock(startTimestamp)
                .lastBlockNumber(recordFile.getIndex() - 1) // Library internally increments last by one for current
                .migrationRecordsStreamed(true)
                .build();
    }
}
