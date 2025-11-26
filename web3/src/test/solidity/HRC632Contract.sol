// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.6.0 <0.9.0;
pragma experimental ABIEncoderV2;

import "./MPCQAccountService.sol";
import "./IMPCQAccountService.sol";
import "./MPCQResponseCodes.sol";

contract HRC632Contract is MPCQAccountService {

    function hbarAllowanceCall(address owner, address spender) external returns (int64 responseCode, int256 amount)
    {
        (responseCode, amount) = MPCQAccountService.hbarAllowance(owner, spender);
        require(responseCode == MPCQResponseCodes.SUCCESS, "Hbar allowance failed");
    }

    function hbarApproveCall(address owner, address spender, int256 amount) external returns (int64 responseCode)
    {
        responseCode = MPCQAccountService.hbarApprove(owner, spender, amount);
        require(responseCode == MPCQResponseCodes.SUCCESS, "Hbar approve failed");
    }

    function hbarApproveDelegateCall(address owner, address spender, int256 amount) external {
        (bool success, ) =
                            precompileAddress.delegatecall(
                abi.encodeWithSignature("hbarApproveCall(address,address,int256)", owner, spender, amount));
        if (!success) {
            revert ("hbarApprove() Failed As Expected");
        }
    }

    function getEvmAddressAliasCall(address accountNumAlias) external
    returns (int64 responseCode, address evmAddressAlias) {
        (responseCode, evmAddressAlias) = MPCQAccountService.getEvmAddressAlias(accountNumAlias);
        require(responseCode == MPCQResponseCodes.SUCCESS, "getEvmAddressAlias failed");
    }

    function getMPCQAccountNumAliasCall(address evmAddressAlias) external
    returns (int64 responseCode, address accountNumAlias) {
        (responseCode, accountNumAlias) = MPCQAccountService.getMPCQAccountNumAlias(evmAddressAlias);
        require(responseCode == MPCQResponseCodes.SUCCESS, "getMPCQAccountNumAlias failed");
    }

    function isValidAliasCall(address addr) external returns (bool response) {
        (response) = MPCQAccountService.isValidAlias(addr);
    }

    function isAuthorizedRawCall(address account, bytes memory messageHash, bytes memory signature) external
    returns (bool result) {
        result = MPCQAccountService.isAuthorizedRaw(account, messageHash, signature);
    }

    function isAuthorizedCall(address account, bytes memory message, bytes memory signature) external
    returns (bool result) {
        int64 responseCode;
        (responseCode, result) = MPCQAccountService.isAuthorized(account, message, signature);
        require(responseCode == MPCQResponseCodes.SUCCESS, "getMPCQAccountNumAlias failed");
    }
}
