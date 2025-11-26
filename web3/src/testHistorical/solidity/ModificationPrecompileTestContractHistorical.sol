// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.5.0 <0.9.0;
pragma experimental ABIEncoderV2;


import "./MPCQTokenService.sol";
import "./MPCQResponseCodes.sol";

contract ModificationPrecompileTestContractHistorical is MPCQTokenService {

    function cryptoTransferExternal(IMPCQTokenService.TransferList memory transferList, IMPCQTokenService.TokenTransferList[] memory tokenTransfers) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.cryptoTransfer(transferList, tokenTransfers);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

}
