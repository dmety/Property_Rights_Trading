pragma experimental ABIEncoderV2;
pragma solidity >=0.4.24 < 0.6.11;

contract BlockTradeDid {
    address private owner;
    string private did;
    bool private status;
    //个人声明信息
    struct Claim {
        address userAddr;   /** 用户的账户地址 */
        string userType;    /** 用户的身份：系统管理员、交易受理人员、交易审核人员、交易鉴证人员、确权机构人员、外网发布人员、监管机构人员、普通用户 */
        string userName;    /** 用户名称 */
        string userOrgan;   /** 用户所在机构名称，普通用户的机构名称为空 */
        string tradeType;    /** 交易种类：A土地类  B林地类  C房屋类 D生产设施类 E知识产权类 */
        uint256 claimTime;  /** 声明时间 */
        uint256 orgId; /** 机构标识 */
        uint256 userId; /** 用户标识 */
    }
    Claim private claim;
    
    //修饰器
    modifier onlyOwner(address _owner) {
        require(owner == _owner, "DID验证：不是合约拥有者！");
        _;
    }

    constructor (address _owner) public {
        owner = _owner;
        claim.userAddr = _owner;
        string memory addrStr = addressToString(_owner);
        did = DIDWrapper(addrStr);
        status = true;
    }
    
    //将 address 转换为 string
    function addressToString(address _addr) private pure returns (string memory) {
        bytes memory addressBytes = abi.encodePacked(_addr);
        bytes memory hexAlphabet = "0123456789abcdef";
        bytes memory str = new bytes(2 + addressBytes.length * 2);
        str[0] = '0';
        str[1] = 'x';
        for (uint i = 0; i < addressBytes.length; i++) {
            str[2+i*2] = hexAlphabet[uint(uint8(addressBytes[i] >> 4))];
            str[3+i*2] = hexAlphabet[uint(uint8(addressBytes[i] & 0x0f))];
        }
        return string(str);
    }
    
    // 包装did标识
    function DIDWrapper(string memory addr) private pure returns(string memory) {
        string memory new_str = string(abi.encodePacked("did", ":", "blocktrade", ":", addr));
        return new_str;
    }

    //得到did标识
    function getDID() public view returns(string memory) {
        return did;
    }
    //设置did有效状态
    function setStatus(bool _status) {
        status = _status;
    }
    
    //设置did身份的状态
    function getStatus() public returns(bool) {
        return status;
    }
    
    //设置个人声明
    function setClaim(address _userAddr, string memory _userType, string memory _userName, 
        string memory _userOrgan, string memory _tradeType, uint256 _orgid, uint256 _userid) public onlyOwner(_userAddr) {
        require(claim.userAddr == _userAddr, "DID验证：声明中的账户地址与调用者不一致！");
        claim.userType = _userType;
        claim.userName = _userName;
        claim.userOrgan = _userOrgan;
        claim.tradeType = _tradeType;
        claim.claimTime = now;
        claim.orgId = _orgid;
        claim.userId = _userid;
    }
    
    //查询个人声明
    function queryClaim() public onlyOwner(msg.sender) returns(address, string memory,  string memory,  
        string memory,  string memory, uint256,uint256,uint256) {
        return (claim.userAddr, claim.userType, claim.userName, claim.userOrgan, claim.tradeType, claim.claimTime, claim.userId, claim.orgId);
    }
}

