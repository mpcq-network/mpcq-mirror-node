// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.4.9 <0.9.0;

import "./MPCQScheduleService.sol";
import "./MPCQResponseCodes.sol";
pragma experimental ABIEncoderV2;

contract HRC755Contract is MPCQScheduleService {
    function authorizeScheduleCall(address schedule) external returns (int64 responseCode)
    {
        (responseCode) = MPCQScheduleService.authorizeSchedule(schedule);
        require(responseCode == MPCQResponseCodes.SUCCESS, "Authorize schedule failed");
    }

    function signScheduleCall(address schedule, bytes memory signatureMap) external returns (int64 responseCode) {
        (responseCode) = MPCQScheduleService.signSchedule(schedule, signatureMap);
        require(responseCode == MPCQResponseCodes.SUCCESS, "Authorize schedule failed");
    }
}
