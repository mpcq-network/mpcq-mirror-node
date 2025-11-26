// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.4.9 <0.9.0;

import "./MPCQScheduleService.sol";
import "./MPCQResponseCodes.sol";
pragma experimental ABIEncoderV2;

contract GetScheduleInfo is MPCQScheduleService {

    function getFungibleCreateTokenInfo(address scheduleAddress) external returns (int64 responseCode, IMPCQTokenService.FungibleTokenInfo memory fungibleTokenInfo) {
        (responseCode, fungibleTokenInfo) = getScheduledCreateFungibleTokenInfo(scheduleAddress);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
        return (responseCode, fungibleTokenInfo);
    }

    function getNonFungibleCreateTokenInfo(address scheduleAddress) external returns (int64 responseCode, IMPCQTokenService.NonFungibleTokenInfo memory nonFungibleTokenInfo) {
        (responseCode, nonFungibleTokenInfo) = getScheduledCreateNonFungibleTokenInfo(scheduleAddress);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
        return (responseCode, nonFungibleTokenInfo);
    }
}
