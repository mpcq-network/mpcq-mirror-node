// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.5.0 <0.9.0;
pragma experimental ABIEncoderV2;


import "./MPCQTokenService.sol";
import "./MPCQResponseCodes.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";

interface IHRC {
    function associate() external returns (uint256 responseCode);

    function dissociate() external returns (uint256 responseCode);

    function isAssociated() external returns (bool associated);
}

contract ModificationPrecompileTestContract is MPCQTokenService {

    uint256 salt = 1234;

    function deployViaCreate2() public returns (address) {
        NestedContract newContract = new NestedContract{salt: bytes32(salt)}();

        return address(newContract);
    }

    function cryptoTransferExternal(IMPCQTokenService.TransferList memory transferList, IMPCQTokenService.TokenTransferList[] memory tokenTransfers) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.cryptoTransfer(transferList, tokenTransfers);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function mintTokenExternal(address token, int64 amount, bytes[] memory metadata) external
    returns (int responseCode, int64 newTotalSupply, int64[] memory serialNumbers)
    {
        (responseCode, newTotalSupply, serialNumbers) = MPCQTokenService.mintToken(token, amount, metadata);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function burnTokenExternal(address token, int64 amount, int64[] memory serialNumbers) external
    returns (int responseCode, int64 newTotalSupply)
    {
        (responseCode, newTotalSupply) = MPCQTokenService.burnToken(token, amount, serialNumbers);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function associateTokensExternal(address account, address[] memory tokens) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.associateTokens(account, tokens);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function associateTokenExternal(address account, address token) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.associateToken(account, token);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function dissociateTokensExternal(address account, address[] memory tokens) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.dissociateTokens(account, tokens);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function dissociateTokenExternal(address account, address token) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.dissociateToken(account, token);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function associate(address token) public returns (uint256 responseCode) {
        return IHRC(token).associate();
    }

    function dissociate(address token) public returns (uint256 responseCode) {
        return IHRC(token).dissociate();
    }

    function isAssociated(address token) public returns (bool associated) {
        return IHRC(token).isAssociated();
    }

    function createFungibleTokenExternal(IMPCQTokenService.MPCQToken memory token,
        int64 initialTotalSupply,
        int32 decimals) external payable
    returns (int responseCode, address tokenAddress)
    {
        (responseCode, tokenAddress) = MPCQTokenService.createFungibleToken(token, initialTotalSupply, decimals);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function createFungibleTokenWithInheritKeysExternal() external payable returns (address)
    {
        IMPCQTokenService.TokenKey[] memory keys = new IMPCQTokenService.TokenKey[](5);
        IMPCQTokenService.KeyValue memory inheritKey;
        inheritKey.inheritAccountKey = true;
        keys[0] = IMPCQTokenService.TokenKey(1, inheritKey);
        keys[1] = IMPCQTokenService.TokenKey(2, inheritKey);
        keys[2] = IMPCQTokenService.TokenKey(4, inheritKey);
        keys[3] = IMPCQTokenService.TokenKey(8, inheritKey);
        keys[4] = IMPCQTokenService.TokenKey(16, inheritKey);

        IMPCQTokenService.Expiry memory expiry = IMPCQTokenService.Expiry(
            0, address(this), 8000000
        );

        IMPCQTokenService.MPCQToken memory token = IMPCQTokenService.MPCQToken(
            "NAME", "SYMBOL", address(this), "memo", true, 1000, false, keys, expiry
        );

        (int responseCode, address tokenAddress) =
                            MPCQTokenService.createFungibleToken(token, 10, 10);

        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert ();
        }

        return tokenAddress;
    }

    function createFungibleTokenWithCustomFeesExternal(IMPCQTokenService.MPCQToken memory token,
        int64 initialTotalSupply,
        int32 decimals,
        IMPCQTokenService.FixedFee[] memory fixedFees,
        IMPCQTokenService.FractionalFee[] memory fractionalFees) external payable
    returns (int responseCode, address tokenAddress)
    {
        (responseCode, tokenAddress) = MPCQTokenService.createFungibleTokenWithCustomFees(token, initialTotalSupply, decimals, fixedFees, fractionalFees);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function createNonFungibleTokenExternal(IMPCQTokenService.MPCQToken memory token) external payable
    returns (int responseCode, address tokenAddress)
    {
        (responseCode, tokenAddress) = MPCQTokenService.createNonFungibleToken(token);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function createNonFungibleTokenWithCustomFeesExternal(IMPCQTokenService.MPCQToken memory token,
        IMPCQTokenService.FixedFee[] memory fixedFees,
        IMPCQTokenService.RoyaltyFee[] memory royaltyFees) external payable
    returns (int responseCode, address tokenAddress)
    {
        (responseCode, tokenAddress) = MPCQTokenService.createNonFungibleTokenWithCustomFees(token, fixedFees, royaltyFees);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function approveExternal(address token, address spender, uint256 amount) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.approve(token, spender, amount);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function transferFromExternal(address token, address from, address to, uint256 amount) external
    returns (int64 responseCode)
    {
        responseCode = this.transferFrom(token, from, to, amount);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function transferFromNFTExternal(address token, address from, address to, uint256 serialNumber) external
    returns (int64 responseCode)
    {
        responseCode = this.transferFromNFT(token, from, to, serialNumber);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function approveNFTExternal(address token, address approved, uint256 serialNumber) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.approveNFT(token, approved, serialNumber);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function freezeTokenExternal(address token, address account) external
    returns (int64 responseCode)
    {
        responseCode = MPCQTokenService.freezeToken(token, account);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function unfreezeTokenExternal(address token, address account) external
    returns (int64 responseCode)
    {
        responseCode = MPCQTokenService.unfreezeToken(token, account);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function grantTokenKycExternal(address token, address account) external
    returns (int64 responseCode)
    {
        responseCode = MPCQTokenService.grantTokenKyc(token, account);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function revokeTokenKycExternal(address token, address account) external
    returns (int64 responseCode)
    {
        responseCode = MPCQTokenService.revokeTokenKyc(token, account);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function setApprovalForAllExternal(address token, address operator, bool approved) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.setApprovalForAll(token, operator, approved);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function transferTokensExternal(address token, address[] memory accountIds, int64[] memory amounts) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.transferTokens(token, accountIds, amounts);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function transferNFTsExternal(address token, address[] memory sender, address[] memory receiver, int64[] memory serialNumber) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.transferNFTs(token, sender, receiver, serialNumber);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function transferTokenExternal(address token, address sender, address receiver, int64 amount) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.transferToken(token, sender, receiver, amount);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function transferNFTExternal(address token, address sender, address receiver, int64 serialNumber) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.transferNFT(token, sender, receiver, serialNumber);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function pauseTokenExternal(address token) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.pauseToken(token);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function unpauseTokenExternal(address token) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.unpauseToken(token);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function wipeTokenAccountExternal(address token, address account, int64 amount) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.wipeTokenAccount(token, account, amount);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function wipeTokenAccountNFTExternal(address token, address account, int64[] memory serialNumbers) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.wipeTokenAccountNFT(token, account, serialNumbers);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function deleteTokenExternal(address token) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.deleteToken(token);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function updateFungibleTokenCustomFeesExternal(address token, IMPCQTokenService.FixedFee[] memory fixedFees, IMPCQTokenService.FractionalFee[] memory fractionalFees) external
    returns (int64 responseCode)
    {
        responseCode = MPCQTokenService.updateFungibleTokenCustomFees(token, fixedFees, fractionalFees);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function getCustomFeesForToken(address token) internal
    returns (
        IMPCQTokenService.FixedFee[] memory fixedFees,
        IMPCQTokenService.FractionalFee[] memory fractionalFees,
        IMPCQTokenService.RoyaltyFee[] memory royaltyFees)
    {
        int responseCode;
        (responseCode, fixedFees, fractionalFees, royaltyFees) = MPCQTokenService.getTokenCustomFees(token);

        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("Failed to fetch custom fees");
        }

        return (fixedFees, fractionalFees, royaltyFees);
    }

    function updateFungibleTokenCustomFeesAndGetExternal(
        address token,
        IMPCQTokenService.FixedFee[] memory fixedFees,
        IMPCQTokenService.FractionalFee[] memory fractionalFees,
        IMPCQTokenService.RoyaltyFee[] memory royaltyFees) external
    returns (
        IMPCQTokenService.FixedFee[] memory newFixedFees,
        IMPCQTokenService.FractionalFee[] memory newFractionalFees,
        IMPCQTokenService.RoyaltyFee[] memory newRoyaltyFees)
    {
        int64 responseCode = MPCQTokenService.updateFungibleTokenCustomFees(token, fixedFees, fractionalFees);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("Failed to update fungible token custom fees");
        }

        (newFixedFees, newFractionalFees, newRoyaltyFees) = getCustomFeesForToken(token);
        return (newFixedFees, newFractionalFees, newRoyaltyFees);
    }

    function updateNonFungibleTokenCustomFeesAndGetExternal(
        address token,
        IMPCQTokenService.FixedFee[] memory fixedFees,
        IMPCQTokenService.FractionalFee[] memory fractionalFees,
        IMPCQTokenService.RoyaltyFee[] memory royaltyFees) external
    returns (
        IMPCQTokenService.FixedFee[] memory newFixedFees,
        IMPCQTokenService.FractionalFee[] memory newFractionalFees,
        IMPCQTokenService.RoyaltyFee[] memory newRoyaltyFees)
    {
        int64 responseCode = MPCQTokenService.updateNonFungibleTokenCustomFees(token, fixedFees, royaltyFees);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert("Failed to update nft custom fees");
        }

        (newFixedFees, newFractionalFees, newRoyaltyFees) = getCustomFeesForToken(token);

        return (newFixedFees, newFractionalFees, newRoyaltyFees);
    }

    function updateTokenKeysExternal(address token, IMPCQTokenService.TokenKey[] memory keys) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.updateTokenKeys(token, keys);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function updateTokenExpiryInfoExternal(address token, IMPCQTokenService.Expiry memory expiryInfo) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.updateTokenExpiryInfo(token, expiryInfo);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function updateTokenInfoExternal(address token, IMPCQTokenService.MPCQToken memory tokenInfo) external
    returns (int responseCode)
    {
        responseCode = MPCQTokenService.updateTokenInfo(token, tokenInfo);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    function associateWithRedirect(address token) external returns (bytes memory result)
    {
        (int response, bytes memory result) = this.redirectForToken(token, abi.encodeWithSelector(IHRC.associate.selector));
        if (response != MPCQResponseCodes.SUCCESS) {
            revert("Tokens association redirect failed");
        }
        return result;
    }

    function dissociateWithRedirect(address token) external returns (bytes memory result)
    {
        (int response, bytes memory result) = this.redirectForToken(token, abi.encodeWithSelector(IHRC.dissociate.selector));
        if (response != MPCQResponseCodes.SUCCESS) {
            revert("Tokens dissociation redirect failed");
        }
        return result;
    }

    function callNotExistingPrecompile(address token) public returns (bytes memory result)
    {
        (int response, bytes memory result) = this.redirectForToken(token, abi.encodeWithSelector(bytes4(keccak256("notExistingPrecompile()"))));
        return result;
    }

    function createContractViaCreate2AndTransferFromIt(address token, address sponsor, address receiver, int64 amount) external
    returns (int responseCode)
    {
        address create2Contract = deployViaCreate2();

        int associateSenderResponseCode = MPCQTokenService.associateToken(create2Contract, token);
        if (associateSenderResponseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }

        int associateRecipientResponseCode = MPCQTokenService.associateToken(receiver, token);
        if (associateRecipientResponseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }

        int grantTokenKycResponseCodeContract = grantTokenKyc(token, create2Contract);
        if (grantTokenKycResponseCodeContract != MPCQResponseCodes.SUCCESS) {
            revert();
        }

        int grantTokenKycResponseCodeReceiver = grantTokenKyc(token, receiver);
        if (grantTokenKycResponseCodeReceiver != MPCQResponseCodes.SUCCESS) {
            revert();
        }

        int sponsorTransferResponseCode = MPCQTokenService.transferToken(token, sponsor, create2Contract, amount / 2);
        if (sponsorTransferResponseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }

        responseCode = MPCQTokenService.transferToken(token, create2Contract, receiver, amount / 4);
        if (responseCode != MPCQResponseCodes.SUCCESS) {
            revert();
        }
    }

    receive() external payable {
    }
}

contract NestedContract {

}