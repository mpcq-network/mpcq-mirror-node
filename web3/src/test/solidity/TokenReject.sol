// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.5.0 <0.9.0;
pragma experimental ABIEncoderV2;

import "./MPCQTokenService.sol";

contract TokenReject is MPCQTokenService {

    function rejectTokens(address rejectingAddress, address[] memory ftAddresses, address[] memory nftAddresses) public returns(int64 responseCode) {
        IMPCQTokenService.NftID[] memory nftIDs = new IMPCQTokenService.NftID[](nftAddresses.length);
        for (uint i; i < nftAddresses.length; i++)
        {
            IMPCQTokenService.NftID memory nftId;
            nftId.nft = nftAddresses[i];
            nftId.serial = 1;
            nftIDs[i] = nftId;
        }
        responseCode = rejectTokens(rejectingAddress, ftAddresses, nftIDs);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
        return responseCode;
    }
}
