pragma experimental ABIEncoderV2;
pragma solidity >=0.4.24 < 0.6.11;

import "./Table.sol";
import "./KVTable.sol";
import "./BlockTradeDid.sol";

contract DidRegCenter {
    address public owner;
    uint256 private version = 1;
    
    event createdDIDEvent(string did);
    event setStatusEvent(bool ok, string did);
    
    modifier onlyOwner(address _owner) {
        require(owner == _owner, "认证: 要求调用者必须是管理员！");
        _;
    }
    
    constructor() public {
        owner = msg.sender;
        createTable();
    }

    function createTable() private {
        // 初始化表工场
        KVTableFactory kvtf = KVTableFactory(0x1010);
        // 创建表
        kvtf.createTable("Document", "did", "context, version, createTime, publicKey");
        // TableName:DID文档 KeyWords:DID, DID文档合约地址, 创建时间, DID用户的公钥
    }

    function openTable() private view returns(KVTable){
        //初始化表工厂
        KVTableFactory kvtf = KVTableFactory(0x1010);
        // 打开Table
        KVTable kvTable = kvtf.openTable("Document");
        return kvTable;
    }
    
    /* 创建DID 传递参数：用户的账户地址、公钥、合约拥有者、用户角色、用户名称、用户所在机构、交易种类、机构标识、用户标 识*/
    function createDID(address _addr, string memory _publicKey, address _owner, string memory _userType, string memory _userName, string memory _userOrgan, string memory _tradeType, uint256 _orgId, uint256 _userId) public onlyOwner(_owner) returns(string memory) {
        // 新建DID文档合约：BlockTradeDid
        BlockTradeDid didContract = new BlockTradeDid(_addr);
        //打开Document
        KVTable kvTable = openTable();
        Entry document = kvTable.newEntry();
        document.set("did", didContract.getDID());
        document.set("context", address(didContract));
        document.set("version", version);
        document.set("createTime", now);
        document.set("publicKey", _publicKey);
        // 填写数据
        kvTable.set(didContract.getDID(), document);
        // 设置声明的值
        didContract.setClaim(_addr, _userType, _userName, _userOrgan, _tradeType, _orgId, _userId);
        // 创建DID事件
        emit createdDIDEvent(didContract.getDID());
        return (didContract.getDID());
    }
    
    // DID文档结构体
    struct Document {
        string did;
        address context;
        uint version;
        uint createTime;
        string publicKey;
    }
    
    //获取文档内容
    function getDocument(string memory _did) public view returns(Document memory) {
        // 打开表
        KVTable kvTable = openTable();
        bool ok = false; //获取是否成功获取到did文档的回调
        Entry entry;
        Document memory document;
        (ok, entry) = kvTable.get(_did);
        if(ok) { // =true ↓
            document.did = entry.getString("did");
            document.context = entry.getAddress("context");
            document.version = entry.getUInt("version");
            document.createTime = entry.getUInt("createTime");
            document.publicKey = entry.getString("publicKey");
        }
        return (document);
    }
    
    // 设置状态
    function setStatus(string memory _did, bool _status, address _owner) onlyOwner(_owner) public  {
        KVTable kvTable = openTable();
        bool ok = false;
        Entry entry;
        address context;
        (ok, entry) = kvTable.get(_did);
        if(ok) {
            context = entry.getAddress("context");
            BlockTradeDid didContract = BlockTradeDid(context);
            didContract.setStatus(_status);
        }
        emit setStatusEvent(ok, _did);
    }
    
    // 获得DID状态，true代表有效，false代表失效
    function getStatus(string memory _did) public view returns(bool) {
        KVTable kvTable = openTable();
        bool ok = false;
        Entry entry;
        address context;
        (ok, entry) = kvTable.get(_did);
        if(ok) {
            context = entry.getAddress("context");
            BlockTradeDid didContract = BlockTradeDid(context);
            return didContract.getStatus();
        }
        return false;
    }
}