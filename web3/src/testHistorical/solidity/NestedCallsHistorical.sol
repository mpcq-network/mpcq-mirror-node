// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.0;

import "./MPCQTokenService.sol";
import "./MPCQResponseCodes.sol";

contract NestedCallsHistorical is MPCQTokenService {

    function nestedGetTokenInfo(address token) external returns (IMPCQTokenService.TokenInfo memory) {
        (int responseCode, IMPCQTokenService.TokenInfo memory retrievedTokenInfo) = MPCQTokenService.getTokenInfo(token);
        return retrievedTokenInfo;
    }

    function nestedHtsGetApproved(address token, uint256 serialNumber) public returns (address) {
        (int _responseCode, address approved) = MPCQTokenService.getApproved(token, serialNumber);
        return approved;
    }

    function nestedMintToken(address token, int64 amount, bytes[] memory metadata) public returns (int64) {
        (int responseCode, int64 newTotalSupply, int64[] memory serialNumbers) = MPCQTokenService.mintToken(token, amount, metadata);
        return newTotalSupply;
    }
}
