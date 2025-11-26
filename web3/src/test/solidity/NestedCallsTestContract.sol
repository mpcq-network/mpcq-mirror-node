// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.0;

import "./MPCQTokenService.sol";
import "./MPCQResponseCodes.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";

contract NestedCalls is MPCQTokenService {

    //Update token key + get token info key
    function updateTokenKeysAndGetUpdatedTokenKey(address token, IMPCQTokenService.TokenKey[] memory keys, uint keyType) external returns (IMPCQTokenService.KeyValue memory) {
        int responseCode = MPCQTokenService.updateTokenKeys(token, keys);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("Could not update token keys.");
        }

        (int response, IMPCQTokenService.KeyValue memory key) = MPCQTokenService.getTokenKey(token, keyType);
        if (response != MPCQResponseCodes.SUCCESS) {
            revert("Could not get token key.");
        }
        return key;
    }

    //Update + get token expiry info
    function updateTokenExpiryAndGetUpdatedTokenExpiry(address token, IMPCQTokenService.Expiry memory expiryInfo) external returns (IMPCQTokenService.Expiry memory) {
        int responseCode = MPCQTokenService.updateTokenExpiryInfo(token, expiryInfo);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("Could not update token expiry info.");
        }

        (int response, IMPCQTokenService.Expiry memory retrievedExpiry) = MPCQTokenService.getTokenExpiryInfo(token);
        if (response != MPCQResponseCodes.SUCCESS) {
            revert("Could not read token expiry info.");
        }

        return retrievedExpiry;
    }

    // Update token info that updates symbol + get token info symbol
    function updateTokenInfoAndGetUpdatedTokenInfoSymbol(address token, IMPCQTokenService.MPCQToken memory tokenInfo) external returns (string memory symbol) {
        int responseCode = MPCQTokenService.updateTokenInfo(token, tokenInfo);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("Could not update token info.");
        }

        (int response, IMPCQTokenService.TokenInfo memory retrievedTokenInfo) = MPCQTokenService.getTokenInfo(token);

        if (response != MPCQResponseCodes.SUCCESS) {
            revert("Could not get token info.");
        }

        return retrievedTokenInfo.token.symbol;
    }

    // Update token info that updates name + get token info name
    function updateTokenInfoAndGetUpdatedTokenInfoName(address token, IMPCQTokenService.MPCQToken memory tokenInfo) external returns (string memory name) {
        int responseCode = MPCQTokenService.updateTokenInfo(token, tokenInfo);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("Could not update token info.");
        }

        (int response, IMPCQTokenService.TokenInfo memory retrievedTokenInfo) = MPCQTokenService.getTokenInfo(token);

        if (response != MPCQResponseCodes.SUCCESS) {
            revert("Could not get token info.");
        }

        return retrievedTokenInfo.token.name;
    }

    // Update token info that updates memo + get token info memo
    function updateTokenInfoAndGetUpdatedTokenInfoMemo(address token, IMPCQTokenService.MPCQToken memory tokenInfo) external returns (string memory memo) {
        int responseCode = MPCQTokenService.updateTokenInfo(token, tokenInfo);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("Could not update token info.");
        }

        (int response, IMPCQTokenService.TokenInfo memory retrievedTokenInfo) = MPCQTokenService.getTokenInfo(token);

        if (response != MPCQResponseCodes.SUCCESS) {
            revert("Could not get token info.");
        }

        return retrievedTokenInfo.token.memo;
    }

    // Update auto renew period + get token info auto renew period
    function updateTokenInfoAndGetUpdatedTokenInfoAutoRenewPeriod(address token, IMPCQTokenService.MPCQToken memory tokenInfo) external returns (int64 autoRenewPeriod) {
        int responseCode = MPCQTokenService.updateTokenInfo(token, tokenInfo);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("Could not update token info.");
        }

        (int response, IMPCQTokenService.TokenInfo memory retrievedTokenInfo) = MPCQTokenService.getTokenInfo(token);

        if (response != MPCQResponseCodes.SUCCESS) {
            revert("Could not get token info.");
        }

        return retrievedTokenInfo.token.expiry.autoRenewPeriod;
    }

    // Delete token + get token info isDeleted
    function deleteTokenAndGetTokenInfoIsDeleted(address token) external returns (bool deleted) {
        int responseCode = MPCQTokenService.deleteToken(token);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("Could not delete token.");
        }

        (int response, IMPCQTokenService.TokenInfo memory retrievedTokenInfo) = MPCQTokenService.getTokenInfo(token);
        if (response != MPCQResponseCodes.SUCCESS) {
            revert("Could not get token info.");
        }

        return retrievedTokenInfo.deleted;
    }

    // Create token for fungible token with/without default freeze status + name + symbol + getTokenDefaultFreezeStatus + getTokenDefaultKycStatus + isToken
    function createFungibleTokenAndGetIsTokenAndGetDefaultFreezeStatusAndGetDefaultKycStatus(IMPCQTokenService.MPCQToken memory token, int64 initialTotalSupply, int32 decimals) external payable returns (
        bool defaultKycStatus,
        bool defaultFreezeStatus,
        bool isToken) {
        (int256 responseCode, address tokenAddress) = MPCQTokenService.createFungibleToken(token, initialTotalSupply, decimals);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("Fungible token could not be created.");
        }

        (responseCode, defaultKycStatus) = MPCQTokenService.getTokenDefaultKycStatus(tokenAddress);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("Could not get token default kyc status.");
        }

        (responseCode, defaultFreezeStatus) = MPCQTokenService.getTokenDefaultFreezeStatus(tokenAddress);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("Could not get token default freeze status.");
        }

        (responseCode, isToken) = MPCQTokenService.isToken(tokenAddress);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("isToken(tokenAddress) returned an error.");
        }
    }

    // Create NFT with/without default freeze status + name + symbol + getTokenDefaultFreezeStatus + getTokenDefaultKycStatus + isToken
    function createNFTAndGetIsTokenAndGetDefaultFreezeStatusAndGetDefaultKycStatus(IMPCQTokenService.MPCQToken memory token) external payable returns (
        bool defaultKycStatus,
        bool defaultFreezeStatus,
        bool isToken) {
        (int256 responseCode, address tokenAddress) = MPCQTokenService.createNonFungibleToken(token);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("NFT could not be created.");
        }

        (responseCode, defaultKycStatus) = MPCQTokenService.getTokenDefaultKycStatus(tokenAddress);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("Could not get token default kyc status.");
        }

        (responseCode, defaultFreezeStatus) = MPCQTokenService.getTokenDefaultFreezeStatus(tokenAddress);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("Could not get token default freeze status.");
        }

        (responseCode, isToken) = MPCQTokenService.isToken(tokenAddress);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("isToken(tokenAddress) returned an error.");
        }
    }

    function nestedGetTokenInfoAndHardcodedResult(address token) external returns (string memory) {
        (int responseCode, IMPCQTokenService.TokenInfo memory retrievedTokenInfo) = MPCQTokenService.getTokenInfo(token);
        return "hardcodedResult";
    }

    function nestedHtsGetApprovedAndHardcodedResult(address token, uint256 serialNumber) public returns (string memory) {
        (int _responseCode, address approved) = MPCQTokenService.getApproved(token, serialNumber);
        return "hardcodedResult";
    }

    function nestedMintTokenAndHardcodedResult(address token, int64 amount, bytes[] memory metadata) public returns (string memory) {
        (int responseCode, int64 newTotalSupply, int64[] memory serialNumbers) = MPCQTokenService.mintToken(token, amount, metadata);
        return "hardcodedResult";
    }

    function deployNestedContracts() public payable returns (address, address, uint256, uint256) {
        require(msg.value >= 30000 wei, "Insufficient funds to deploy contracts");

        // Deploy contracts with minimal balance
        MockContract newContract1 = (new MockContract){value: 10000 wei}();
        MockContract newContract2 = (new MockContract){value: 20000 wei}();

        // Get the balance of each contract
        uint256 contract1Balance = address(newContract1).balance;
        uint256 contract2Balance = address(newContract2).balance;

        // Return contract addresses and their balances
        return (address(newContract1), address(newContract2), contract1Balance, contract2Balance);
    }
}

contract MockContract {

    constructor() payable {}

    function getAddress() public view returns (address) {
        return address(this);
    }

    function destroy() public {
        selfdestruct(payable(msg.sender));
    }

    receive() external payable {}
}
