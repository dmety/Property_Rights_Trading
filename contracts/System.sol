pragma experimental ABIEncoderV2;
pragma solidity >=0.4.24 < 0.6.11;

import './BytesUtils.sol';
import './DidRegCenter.sol';

contract System {
    address didContractAddr;
    constructor(address _didContractAddr) public {
        didContractAddr=_didContractAddr;
        owner=msg.sender;
    }
    
    address private owner;
    modifier onlyOwner {
        require(owner == msg.sender, "认证: 要求调用者必须是发布者");
        _;
    }
    
    uint maxOrgId;
    uint maxUserId;
    // 机构结构体
    struct Organ {
        uint id;
        string orgCode;  // 社会信用代码
        string name;
        string orgType; // 交易中心 确权机构 监管机构
        string zcode;     // 区域代码
        string transKind; //单个交易种类 0 不限 ABCDE 
    }

    // 用户结构体
    struct User {
        uint  id;
        string cardNo;//身份证
        address userAddr;
        string name;
        string role;//确权机构人员 ，交易受理人员，交易审核人员，交易鉴证人员，外网发布人员，监管机构人员普通用户
        uint orgId;
        bool status;
    }
    
    uint[] organs;//存储机构id
    uint[] users;//存储用户id

    // 机构映射 (使用 id 作为映射的主键)
    mapping(uint => Organ) public orgMap;
    mapping(uint => User) public userMap;
    
    // 创建并存储用户
    function createUser(
        address _userAddr,
        string memory _cardNo,
        string memory _name,
        string memory _role,
        uint _orgId,
        string memory _publicKey
    ) public onlyOwner returns(User memory){
        require(DidRegCenter(didContractAddr).getDocument(DIDWrapper(_userAddr)).context==address(0),'该用户已存在did');
        maxUserId++;
        User memory newUser = User({
            id: maxUserId,
            cardNo:_cardNo,
            userAddr: _userAddr,
            name: _name,
            role: _role,
            orgId:_orgId,
            status: true
        });
        userMap[maxUserId] = newUser;
        //创建did
        string memory didInfo=
        DidRegCenter(didContractAddr).createDID(_userAddr,_publicKey,owner,
        _role,_name,orgMap[_orgId].name,orgMap[_orgId].transKind,_orgId,maxUserId);//检查
        require(keccak256(abi.encodePacked(didInfo))!=keccak256(abi.encodePacked('')),'did为空');
        
        users.push(maxUserId);
        return userMap[maxUserId];
    }
    
    //通过城市和机构类型获得机构id和name
    function getOrgans(string memory _zcode,string memory _orgType) public returns(string memory,string memory){
        string memory orgIds;
        string  memory orgNames;
        for(uint i=0;i<organs.length;i++){
            Organ memory organ=orgMap[organs[i]];
            if (keccak256(abi.encodePacked(organ.zcode))==keccak256(abi.encodePacked(_zcode))){
                if( keccak256(abi.encodePacked(organ.orgType))==keccak256(abi.encodePacked(_orgType))){
                    if( bytes(orgIds).length==0){
                        orgIds=BytesUtils.uint2str(organ.id);
                    }else{
                       orgIds= BytesUtils.concat(orgIds,",");
                       orgIds= BytesUtils.concat(orgIds,BytesUtils.uint2str(organ.id));
                    }
                    if( bytes(orgNames).length==0){
                        orgNames=organ.name;
                    }else{
                        orgNames=BytesUtils.concat(orgNames,",");
                        orgNames=BytesUtils.concat(orgNames,organ.name);
                    }
                }
            }
            
        }
        return (orgIds,orgNames);
    }
    
    //id获得
    function getOrgan(uint _id) public returns(Organ memory){
        return orgMap[_id];
    }
    // 根据 ID 返回用户信息的函数
    function getUser(uint _id) public view returns (User memory) {
        return userMap[_id];
    }
    
    // 创建并存储机构
    function createOrgan (
        string memory _orgCode,
        string memory _name,
        string memory _orgType,
        string memory _zcode,
        string memory _transKind
    ) public  onlyOwner returns (Organ memory){
        // require(orgMap[_orgId].id ==0,"该机构已存在");
        maxOrgId++;
        Organ memory newOrgan = Organ({
            id: maxOrgId,
            orgCode: _orgCode,
            name: _name,
            orgType: _orgType,
            zcode: _zcode,
            transKind: _transKind
        });
        orgMap[maxOrgId] = newOrgan;
        organs.push(maxOrgId);
        return orgMap[maxOrgId];
    }
     
    function updateOrgan (
        uint _orgId,
        string memory _orgCode,
        string memory _name,
        string memory _orgType,
        string memory _zcode,
        string memory _transKind
    ) public  onlyOwner returns (Organ memory){
        orgMap[_orgId].orgCode=_orgCode;
        orgMap[_orgId].name=_name;
        orgMap[_orgId].orgType=_orgType;
        orgMap[_orgId].zcode=_zcode;
        orgMap[_orgId].transKind=_transKind;
        return orgMap[_orgId];
    }
    
    //删除机构
    function deleteOrgan(uint _orgId)public onlyOwner{
         require(orgMap[_orgId].id !=0,"该机构不存在");
         for(uint i=0;i<organs.length;i++){
            if( orgMap[organs[i]].id==_orgId){
                delete orgMap[organs[i]];
                organs[i]=0;
            }
        }
        
    }
    
    // 将 address 转换为 string
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
    
    function DIDWrapper(address addr) private pure returns(string memory) {
        string memory addrStr=addressToString(addr);
        string memory new_str = string(abi.encodePacked("did", ":",
        "blocktrade", ":", addrStr));
        return new_str;
    }

    function updateUser(
        uint _userId,
        address _userAddr,
        string memory _cardNo,
        string memory _name,
        string memory _role,
        uint _orgId
    ) public onlyOwner returns(User memory){
        userMap[_userId].userAddr=_userAddr;
        userMap[_userId].cardNo=_cardNo;
        userMap[_userId].name=_name;
        userMap[_userId].role=_role;
        userMap[_userId].orgId=_orgId;
        return userMap[_userId];
    }
    
    
    //删除用户
    function deleteUser(uint _userId,string memory _did) public onlyOwner {
        require(userMap[_userId].id !=0,"该用户不存在");
       for(uint i=0;i<users.length;i++){
            if( userMap[users[i]].id==_userId){
                delete userMap[users[i]];
                users[i]=0;
                DidRegCenter(didContractAddr).setStatus(_did,false,owner);
                break;
            }
        }
         
        
    }
    
    //设置用户状态
    function setUserStatus(bool _status,uint _id,string memory _did) public onlyOwner {
        require(userMap[_id].id !=0,"该用户不存在");
        userMap[_id].status=_status;
        DidRegCenter(didContractAddr).setStatus(_did,_status,owner);
    }
    function grtUserStatus(string memory _did) public onlyOwner returns(bool){
        return DidRegCenter(didContractAddr).getStatus(_did);
    }
}
