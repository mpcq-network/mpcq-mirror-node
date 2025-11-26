// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.4.9 <0.9.0;

import "./MPCQScheduleService.sol";
import "./MPCQTokenService.sol";
import "./MPCQResponseCodes.sol";
import "./KeyHelper.sol";
pragma experimental ABIEncoderV2;

contract HIP756Contract is MPCQScheduleService, KeyHelper {

    function scheduleCreateFT(address autoRenew, address treasury) external payable returns (int64 responseCode, address scheduleAddress) {
        IMPCQTokenService.MPCQToken memory token;
        token.treasury = treasury;
        IMPCQTokenService.Expiry memory expiry;
        expiry.autoRenewAccount = autoRenew;
        token.expiry = expiry;

        token.name = "test";
        token.symbol = "TTT";

        bytes memory tokenCreateBytes = abi.encodeWithSelector(IMPCQTokenService.createFungibleToken.selector, token, 1000, 10);
        (responseCode, scheduleAddress) = scheduleNative( address(0x167), tokenCreateBytes, address(this));
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("Failed to associate");
        }
    }

    function scheduleCreateFTWithDesignatedPayer(address autoRenew, address treasury, address payer) external payable returns (int64 responseCode, address scheduleAddress) {
        IMPCQTokenService.MPCQToken memory token;
        token.treasury = treasury;
        IMPCQTokenService.Expiry memory expiry;
        expiry.autoRenewAccount = autoRenew;
        token.expiry = expiry;

        token.name = "test with designated payer";
        token.symbol = "TTTP";

        bytes memory tokenCreateBytes = abi.encodeWithSelector(IMPCQTokenService.createFungibleToken.selector, token, 1000, 10);
        (responseCode, scheduleAddress) = scheduleNative( address(0x167), tokenCreateBytes, payer);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("Failed to associate");
        }
    }

    function scheduleCreateNFT(address autoRenew, address treasury) external payable returns (int64 responseCode, address scheduleAddress) {
        IMPCQTokenService.MPCQToken memory token;
        token.name = "nft";
        token.symbol = "nft";
        token.treasury = address(treasury);
        IMPCQTokenService.Expiry memory expiry;
        expiry.autoRenewAccount = autoRenew;
        token.expiry = expiry;
        token.tokenKeys = new IMPCQTokenService.TokenKey[](1);
        IMPCQTokenService.TokenKey memory tokenSupplyKey = KeyHelper.getSingleKey(4, 2, address(this));
        token.tokenKeys[0] = tokenSupplyKey;
        bytes memory tokenCreateBytes = abi.encodeWithSelector(IMPCQTokenService.createNonFungibleToken.selector, token);
        (responseCode, scheduleAddress) = scheduleNative(address(0x167), tokenCreateBytes, address(this));
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function scheduleCreateNFTWithDesignatedPayer(
        address autoRenew,
        address treasury,
        address payer) external payable returns (int64 responseCode, address scheduleAddress) {
        IMPCQTokenService.MPCQToken memory token;
        token.name = "nft with designated payer";
        token.symbol = "nftp";
        token.treasury = address(treasury);
        IMPCQTokenService.Expiry memory expiry;
        expiry.autoRenewAccount = autoRenew;
        token.expiry = expiry;
        token.tokenKeys = new IMPCQTokenService.TokenKey[](1);
        IMPCQTokenService.TokenKey memory tokenSupplyKey = KeyHelper.getSingleKey(4, 2, address(this));
        token.tokenKeys[0] = tokenSupplyKey;
        bytes memory tokenCreateBytes = abi.encodeWithSelector(IMPCQTokenService.createNonFungibleToken.selector, token);
        (responseCode, scheduleAddress) = scheduleNative(address(0x167), tokenCreateBytes, payer);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function scheduleUpdateTreasuryAndAutoRenewAcc(
        address tokenAddress,
        address treasuryAddress,
        address autoRenewAddress,
        string memory name,
        string memory symbol,
        string memory memo) external returns (int64 responseCode, address scheduleAddress) {
        IMPCQTokenService.MPCQToken memory token;
        token.name = name;
        token.symbol = symbol;
        token.treasury = treasuryAddress;
        token.memo = memo;
        IMPCQTokenService.Expiry memory expiry;
        expiry.autoRenewAccount = autoRenewAddress;
        token.expiry = expiry;

        bytes memory tokenUpdateBytes = abi.encodeWithSelector(IMPCQTokenService.updateTokenInfo.selector, tokenAddress, token);
        (responseCode, scheduleAddress) = scheduleNative(address(0x167), tokenUpdateBytes, address(this));

        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert ("Update of tokenInfo.treasury failed!");
        }
    }

    function scheduleUpdateTreasuryAndAutoRenewAccWithDesignatedPayer(address tokenAddress, address treasuryAddress, address autoRenewAddress, string memory name, string memory symbol, string memory memo, address designatedPayer) external returns (int64 responseCode, address scheduleAddress) {
        IMPCQTokenService.MPCQToken memory token;
        token.name = name;
        token.symbol = symbol;
        token.treasury = treasuryAddress;
        token.memo = memo;
        IMPCQTokenService.Expiry memory expiry;
        expiry.autoRenewAccount = autoRenewAddress;
        token.expiry = expiry;

        bytes memory tokenUpdateBytes = abi.encodeWithSelector(IMPCQTokenService.updateTokenInfo.selector, tokenAddress, token);
        (responseCode, scheduleAddress) = scheduleNative(address(0x167), tokenUpdateBytes, designatedPayer);

        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert ("Update of tokenInfo.treasury failed!");
        }
    }
}