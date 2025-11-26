// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.0;
pragma experimental ABIEncoderV2;

import "./MPCQTokenService.sol";
import "./MPCQResponseCodes.sol";

contract PrecompileTestContractHistorical is MPCQTokenService {


    function isTokenAddress(address token) external returns (bool){
        (int response,bool tokenFlag) = MPCQTokenService.isToken(token);

        if (response != MPCQResponseCodes.SUCCESS) {
            revert ("Token isTokenAddress failed!");
        }
        return tokenFlag;
    }

    function isTokenFrozen(address token, address account) external returns (bool){
        (int response,bool frozen) = MPCQTokenService.isFrozen(token, account);

        if (response != MPCQResponseCodes.SUCCESS) {
            revert ("Token isFrozen failed!");
        }
        return frozen;
    }

    function isKycGranted(address token, address account) external returns (bool){
        (int response,bool kycGranted) = MPCQTokenService.isKyc(token, account);

        if (response != MPCQResponseCodes.SUCCESS) {
            revert ("Token isKyc failed!");
        }
        return kycGranted;
    }

    function getTokenDefaultFreeze(address token) external returns (bool) {
        (int response,bool frozen) = MPCQTokenService.getTokenDefaultFreezeStatus(token);

        if (response != MPCQResponseCodes.SUCCESS) {
            revert ("getTokenDefaultFreezeStatus failed!");
        }
        return frozen;
    }

    function getTokenDefaultKyc(address token) external returns (bool) {
        (int response,bool kyc) = MPCQTokenService.getTokenDefaultKycStatus(token);

        if (response != MPCQResponseCodes.SUCCESS) {
            revert ("getTokenDefaultKycStatus failed!");
        }
        return kyc;
    }

    function getCustomFeesForToken(address token) external returns (
        IMPCQTokenService.FixedFee[] memory fixedFees,
        IMPCQTokenService.FractionalFee[] memory fractionalFees,
        IMPCQTokenService.RoyaltyFee[] memory royaltyFees) {
        int responseCode;
        (responseCode, fixedFees, fractionalFees, royaltyFees) = MPCQTokenService.getTokenCustomFees(token);

        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert ();
        }
    }

    function getInformationForToken(address token) external returns (IMPCQTokenService.TokenInfo memory tokenInfo) {
        (int responseCode, IMPCQTokenService.TokenInfo memory retrievedTokenInfo) = MPCQTokenService.getTokenInfo(token);

        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert ();
        }

        tokenInfo = retrievedTokenInfo;
    }

    function getInformationForFungibleToken(address token) external returns (IMPCQTokenService.FungibleTokenInfo memory fungibleTokenInfo) {
        (int responseCode, IMPCQTokenService.FungibleTokenInfo memory retrievedTokenInfo) = MPCQTokenService.getFungibleTokenInfo(token);

        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert ();
        }

        fungibleTokenInfo = retrievedTokenInfo;
    }

    function getInformationForNonFungibleToken(address token, int64 serialNumber) external returns (IMPCQTokenService.NonFungibleTokenInfo memory nonFungibleTokenInfo) {
        (int responseCode, IMPCQTokenService.NonFungibleTokenInfo memory retrievedTokenInfo) = MPCQTokenService.getNonFungibleTokenInfo(token, serialNumber);

        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert ();
        }

        nonFungibleTokenInfo = retrievedTokenInfo;
    }

    function getType(address token) external returns (int) {
        (int statusCode, int tokenType) = MPCQTokenService.getTokenType(token);

        if (statusCode != MPCQResponseCodes.SUCCESS) {
            revert ("Token type appraisal failed!");
        }
        return tokenType;
    }

    function getExpiryInfoForToken(address token) external returns (
        IMPCQTokenService.Expiry memory expiry) {
        (int responseCode,
            IMPCQTokenService.Expiry memory retrievedExpiry) = MPCQTokenService.getTokenExpiryInfo(token);

        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert ();
        }

        expiry = retrievedExpiry;
    }

    function getTokenKeyPublic(address token, uint keyType) public returns (IMPCQTokenService.KeyValue memory) {
        (int responseCode, IMPCQTokenService.KeyValue memory  key) = MPCQTokenService.getTokenKey(token, keyType);
        //"(bool,address,bytes,bytes,address)";


        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }

        return key;
    }

    function htsGetApproved(address token, uint256 serialNumber) public returns (address approved) {
        int _responseCode;
        (_responseCode, approved) = MPCQTokenService.getApproved(token, serialNumber);
    }

    function htsAllowance(address token, address owner, address spender) public returns (uint256 amount){
        int _responseCode;
        (_responseCode, amount) = MPCQTokenService.allowance(token, owner, spender);
    }

    function htsIsApprovedForAll(address token, address owner, address operator) public returns (bool approved) {
        int _responseCode;
        (_responseCode, approved) = MPCQTokenService.isApprovedForAll(token, owner, operator);
    }

    function callMissingPrecompile() public returns (bool success, bytes memory result) {
        (success, result) = address(0x167).call(
            abi.encodeWithSignature("fakeSignature()"));
        require(success);
    }

    function hrcIsAssociated(address token) public returns (bool isAssociated) {
        isAssociated = IHRC(token).isAssociated();
    }
}

interface IHRC {
    function isAssociated() external returns (bool associated);
}
