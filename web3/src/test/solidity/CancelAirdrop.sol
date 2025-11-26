// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.5.0 <0.9.0;
pragma experimental ABIEncoderV2;

import "./MPCQTokenService.sol";

contract CancelAirdrop is MPCQTokenService {

    function cancelAirdrop(address sender, address receiver, address token) public returns(int64 responseCode){
        IMPCQTokenService.PendingAirdrop[] memory pendingAirdrops = new IMPCQTokenService.PendingAirdrop[](1);

        IMPCQTokenService.PendingAirdrop memory pendingAirdrop;
        pendingAirdrop.sender = sender;
        pendingAirdrop.receiver = receiver;
        pendingAirdrop.token = token;

        pendingAirdrops[0] = pendingAirdrop;

        responseCode = cancelAirdrops(pendingAirdrops);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
        return responseCode;
    }

    function cancelNFTAirdrop(address sender, address receiver, address token, int64 serial) public returns(int64 responseCode){
        IMPCQTokenService.PendingAirdrop[] memory pendingAirdrops = new IMPCQTokenService.PendingAirdrop[](1);

        IMPCQTokenService.PendingAirdrop memory pendingAirdrop;
        pendingAirdrop.sender = sender;
        pendingAirdrop.receiver = receiver;
        pendingAirdrop.token = token;
        pendingAirdrop.serial = serial;

        pendingAirdrops[0] = pendingAirdrop;

        responseCode = cancelAirdrops(pendingAirdrops);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
        return responseCode;
    }

    function cancelAirdrops(address[] memory senders, address[] memory receivers, address[] memory tokens, int64[] memory serials) public returns (int64 responseCode) {
        uint length = senders.length;
        IMPCQTokenService.PendingAirdrop[] memory pendingAirdrops = new IMPCQTokenService.PendingAirdrop[](length);
        for (uint i = 0; i < length; i++) {
            IMPCQTokenService.PendingAirdrop memory pendingAirdrop;
            pendingAirdrop.sender = senders[i];
            pendingAirdrop.receiver = receivers[i];
            pendingAirdrop.token = tokens[i];
            pendingAirdrop.serial = serials[i];

            pendingAirdrops[i] = pendingAirdrop;
        }

        responseCode = cancelAirdrops(pendingAirdrops);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
        return responseCode;
    }
}
