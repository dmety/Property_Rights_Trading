pragma solidity >=0.4.24 < 0.6.11;

import "./Table.sol";

contract KVTableFactory {
    function openTable(string memory) public view returns (KVTable) {}
    function createTable(string memory, string memory, string memory) public returns (int256) {}
}
 
//KVTable per permiary key has only one Entry
contract KVTable {
    function get(string memory) public view returns (bool, Entry) {}
    function set(string memory, Entry) public returns (int256) {}
    function newEntry() public view returns (Entry) {}
}