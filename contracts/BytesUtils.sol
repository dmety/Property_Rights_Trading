// SPDX-License-Identifier: LGPL-3.0-or-later
pragma solidity >=0.4.24 <0.6.11;

library BytesUtils {
    
    /* @notice      转换字节数组为地址
    *  @param _bs   源字节数组: 长度必须为20
    *  @return      转换后的地址
    */
    function bytesToAddress(bytes memory _bs) internal pure returns (address addr) {
        require(_bs.length == 20, "bytes length does not match address");
        assembly {
            addr := mload(add(_bs, 0x14))
        }
    }

    /* @notice      转换地址为字节数组
    *  @param _addr 地址
    *  @return      转换后的字节数组
    */
    function addressToBytes(address _addr) internal pure returns (bytes memory bs) {
        assembly {
            bs := mload(0x40)
            mstore(bs, 0x14)
            mstore(add(bs, 0x20), mul(_addr, 0x1000000000000000000000000)) // 修改为兼容 Byzantium
            mstore(0x40, add(bs, 0x40))
        }
    }

    /* @notice              拼接两个字符串
    *  @param _a            字符串1
    *  @param _b            字符串2
    *  @return              返回拼接后字符串
    */
    function concat(string memory _a, string memory _b) public pure returns (string memory) {
        bytes memory bytesA = bytes(_a);
        bytes memory bytesB = bytes(_b);
        string memory result = new string(bytesA.length + bytesB.length);
        bytes memory bytesResult = bytes(result);

        uint k = 0;
        for (uint i = 0; i < bytesA.length; i++) {
            bytesResult[k++] = bytesA[i];
        }
        for (uint j = 0; j < bytesB.length; j++) { // 改为 j
            bytesResult[k++] = bytesB[j];
        }

        return string(bytesResult);
    }

    /* @notice              判断地址是否为合约
    *  @param account       地址
    *  @return              返回布尔值
    */
    function isContract(address account) internal view returns (bool) {
        uint256 codehash;
        assembly {
            codehash := extcodesize(account) // 兼容性修改
        }
        return (codehash > 0);
    }

    /* @notice              转换uint8整数值为一个16进制字符
    *  @param c             uint8整数值
    *  @return              返回uint8
    */
    function fromHexChar(uint8 c) public pure returns (uint8) {
        if (byte(c) >= byte('0') && byte(c) <= byte('9')) {
            return c - uint8(byte('0'));
        }
        if (byte(c) >= byte('a') && byte(c) <= byte('f')) {
            return 10 + c - uint8(byte('a'));
        }
        if (byte(c) >= byte('A') && byte(c) <= byte('F')) {
            return 10 + c - uint8(byte('A'));
        }
        revert();
    }

    /* @notice              转换字符串为原始的字节数组
    *  @param s             字符串
    *  @return              字节数组
    */
    function fromHex(string memory s) public pure returns (bytes memory) {
        bytes memory ss = bytes(s);
        require(ss.length % 2 == 0);
        // length must be even
        bytes memory r = new bytes(ss.length / 2);
        for (uint i = 0; i < ss.length / 2; ++i) {
            r[i] = byte(fromHexChar(uint8(ss[2 * i])) * 16 +
                fromHexChar(uint8(ss[2 * i + 1])));
        }
        return r;
    }

    /* @notice              转换uint为字符串
    *  @param _i            整数值
    *  @return              字符串
    */
    function uint2str(uint _i) public returns (string memory _uintAsString) {
        if (_i == 0) {
            return "0";
        }
        uint j = _i;
        uint len;
        while (j != 0) {
            len++;
            j /= 10;
        }
        bytes memory bstr = new bytes(len);
        uint k = len - 1;
        while (_i != 0) {
            bstr[k--] = byte(uint8(48 + _i % 10));
            _i /= 10;
        }
        return string(bstr);
    }
}
