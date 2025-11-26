// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.0;
pragma experimental ABIEncoderV2;

import "./MPCQTokenService.sol";
import "./MPCQResponseCodes.sol";
import "./ExpiryHelper.sol";
import "./KeyHelper.sol";

contract ComplexFunctions is MPCQTokenService, ExpiryHelper, KeyHelper {
    function tokenLifecycle(address acc1, address acc2, address treasury) public payable {
        IMPCQTokenService.TokenKey[] memory keys = new IMPCQTokenService.TokenKey[](5);
        keys[0] = getSingleKey(KeyType.ADMIN, KeyType.PAUSE, KeyValueType.INHERIT_ACCOUNT_KEY, bytes(""));
        keys[1] = getSingleKey(KeyType.KYC, KeyValueType.INHERIT_ACCOUNT_KEY, bytes(""));
        keys[2] = getSingleKey(KeyType.FREEZE, KeyValueType.INHERIT_ACCOUNT_KEY, bytes(""));
        keys[3] = getSingleKey(KeyType.WIPE, KeyValueType.INHERIT_ACCOUNT_KEY, bytes(""));
        keys[4] = getSingleKey(KeyType.SUPPLY, KeyValueType.INHERIT_ACCOUNT_KEY, bytes(""));

        IMPCQTokenService.Expiry memory expiry = IMPCQTokenService.Expiry(0, treasury, 8000000);

        IMPCQTokenService.MPCQToken memory token = IMPCQTokenService.MPCQToken("TKN", "TK", treasury, "memo", true, 1000000, false, keys, expiry);
        (int code, address tokenAddr) = MPCQTokenService.createFungibleToken(token, 1000000, 8);
        require(code == MPCQResponseCodes.SUCCESS, "Token creation failed");

        require(MPCQTokenService.associateToken(acc1, tokenAddr) == MPCQResponseCodes.SUCCESS, "Token Associate of failed for acc1");
        require(MPCQTokenService.associateToken(acc2, tokenAddr) == MPCQResponseCodes.SUCCESS, "Token Associate failed for acc2");
        require(MPCQTokenService.grantTokenKyc(tokenAddr, acc1) == MPCQResponseCodes.SUCCESS, "GrantKyc failed for acc1");
        require(MPCQTokenService.grantTokenKyc(tokenAddr, acc2) == MPCQResponseCodes.SUCCESS, "GrantKyC failed for acc2");
        require(MPCQTokenService.transferToken(tokenAddr, treasury, acc1, 100) == MPCQResponseCodes.SUCCESS, "Transfer token failed from treasury to acc1");
        require(MPCQTokenService.freezeToken(tokenAddr, acc1) == MPCQResponseCodes.SUCCESS, "Freeze token failed for acc1");
        require(MPCQTokenService.unfreezeToken(tokenAddr, acc1) == MPCQResponseCodes.SUCCESS, "Unfreeze token failed for acc1");
        require(MPCQTokenService.transferToken(tokenAddr, acc1, acc2, 50) == MPCQResponseCodes.SUCCESS, "Transfer token failed from acc1 to acc2");
        require(MPCQTokenService.wipeTokenAccount(tokenAddr, acc2, 10) == MPCQResponseCodes.SUCCESS, "Wipe token failed for acc2");
        require(MPCQTokenService.pauseToken(tokenAddr) == MPCQResponseCodes.SUCCESS, "Pause token failed");
        require(MPCQTokenService.unpauseToken(tokenAddr) == MPCQResponseCodes.SUCCESS, "Unpause token failed");
    }

    function nftLifecycle(address acc1, address acc2, address treasury, bytes[] memory metadata) public payable {
        IMPCQTokenService.TokenKey[] memory keys = new IMPCQTokenService.TokenKey[](5);
        keys[0] = getSingleKey(KeyType.ADMIN, KeyType.PAUSE, KeyValueType.INHERIT_ACCOUNT_KEY, bytes(""));
        keys[1] = getSingleKey(KeyType.KYC, KeyValueType.INHERIT_ACCOUNT_KEY, bytes(""));
        keys[2] = getSingleKey(KeyType.FREEZE, KeyValueType.INHERIT_ACCOUNT_KEY, bytes(""));
        keys[3] = getSingleKey(KeyType.WIPE, KeyValueType.INHERIT_ACCOUNT_KEY, bytes(""));
        keys[4] = getSingleKey(KeyType.SUPPLY, KeyValueType.INHERIT_ACCOUNT_KEY, bytes(""));

        IMPCQTokenService.MPCQToken memory token = IMPCQTokenService.MPCQToken(
            "NFT", "NFT", treasury, "memo", false, 0, false, keys, IMPCQTokenService.Expiry(0, treasury, 8000000)
        );
        (int code, address tokenAddr) = MPCQTokenService.createNonFungibleToken(token);
        require(code == MPCQResponseCodes.SUCCESS, "NFT creation failed");

        require(MPCQTokenService.associateToken(acc1, tokenAddr) == MPCQResponseCodes.SUCCESS, "Associate failed for acc1");
        require(MPCQTokenService.associateToken(acc2, tokenAddr) == MPCQResponseCodes.SUCCESS, "Associate failed for acc2");
        require(MPCQTokenService.grantTokenKyc(tokenAddr, acc1) == MPCQResponseCodes.SUCCESS, "KYC failed for acc1");
        require(MPCQTokenService.grantTokenKyc(tokenAddr, acc2) == MPCQResponseCodes.SUCCESS, "KYC failed for acc2");

        (int mintResponse, , int64[] memory serials) =
                            MPCQTokenService.mintToken(tokenAddr, 0, metadata);

        int64 firstSerial = serials[0];

        require(MPCQTokenService.transferNFT(tokenAddr, treasury, acc1, firstSerial) == MPCQResponseCodes.SUCCESS, "NFT transfer to acc1 failed");
        require(MPCQTokenService.freezeToken(tokenAddr, acc1) == MPCQResponseCodes.SUCCESS, "Freeze failed for acc1");
        require(MPCQTokenService.unfreezeToken(tokenAddr, acc1) == MPCQResponseCodes.SUCCESS, "Unfreeze failed for acc1");
        require(MPCQTokenService.transferNFT(tokenAddr, acc1, acc2, firstSerial) == MPCQResponseCodes.SUCCESS, "NFT transfer to acc2 failed");
        require(MPCQTokenService.wipeTokenAccountNFT(tokenAddr, acc2, serials) == MPCQResponseCodes.SUCCESS, "Wipe failed for acc2");
        require(MPCQTokenService.pauseToken(tokenAddr) == MPCQResponseCodes.SUCCESS, "Pause failed");
        require(MPCQTokenService.unpauseToken(tokenAddr) == MPCQResponseCodes.SUCCESS, "Unpause failed");
    }
}