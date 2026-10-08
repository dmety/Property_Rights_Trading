pragma experimental ABIEncoderV2;
pragma solidity >=0.4.24 < 0.6.11;

contract Right {
    // 产权公共信息结构体
    struct CommonRight {
        string rightNo;          // 产权编号
        string rightName;        // 权证名称
        uint orgId;       // 确权机构
        string rightCertCode;    // 权证编号
        string rightOwner;       // 权利人
        uint userId;      // 确权机构人员Id
        uint8 ownerShip;         // 权属性质（0国有 1集体所有 2个人）
        RightAddress rightAddress;
        string useStartDate;    // 使用期限起始
        string useEndDate;      // 使用期限终止
        string rightCardId;           // 证件号码
    }
    // 产权坐落结构体
    struct RightAddress{
        string zcode;
        string province;
        string city;
        string region;
        string town;//座落乡镇名称
        string village;//座落村名
        string group;//座落组名称
    }
    // 产权四至结构体
    struct FourAddr{
        string FourEast;         // 四至东
        string FourWest;         // 四至西
        string FourSouth;        // 四至南
        string FourNorth;         // 四至北
    }
    
    // 土地注册信息
    struct LandDetails {
        string landNature;        // 农地性质（耕地 四荒地 草地 农田 养殖水面）
        uint256 landArea;        // 土地面积（亩）
        FourAddr fourAddr;
    }

    //添加产权公共信息
    function addCommonInfo(
        string  memory _rightNo,
        string memory _rightName,
        uint _orgId,
        string memory _rightCertCode,
        string memory _rightOwner,
        uint _userId,
        uint8 _ownerShip,
        string memory _useStartDate,
        string memory _useEndDate,
        string memory _rightCardId
    ) public {
         commonRights[_rightNo].rightNo = _rightNo;
         commonRights[_rightNo].rightName = _rightName;
         commonRights[_rightNo].orgId = _orgId;
         commonRights[_rightNo].rightCertCode = _rightCertCode;
         commonRights[_rightNo].rightOwner = _rightOwner;
         commonRights[_rightNo].userId = _userId;
         commonRights[_rightNo].ownerShip = _ownerShip;
         commonRights[_rightNo].useStartDate = _useStartDate;
         commonRights[_rightNo].useEndDate = _useEndDate;
         commonRights[_rightNo].rightCardId = _rightCardId;//添加至映射
         allOrgans.push(_orgId);
         orgRightNo[_orgId].push(_rightNo);
         rightNos.push(_rightNo);
         MyRights[_rightCardId].push(_rightNo);
    }

    //添加坐落信息
    function fillRightAddress(
        string memory _rightNo,
        string memory _zcode,
        string memory _province,
        string memory _city,
        string memory _region,
        string memory _town,
        string memory _village,
        string memory _group
    ) public {
        // 检查产权编号是否存在于映射中
        require(bytes(commonRights[_rightNo].rightNo).length != 0, "找不到产权");
        // 填入地址信息
        commonRights[_rightNo].rightAddress.zcode=_zcode;
        commonRights[_rightNo].rightAddress.province=_province;
        commonRights[_rightNo].rightAddress.city=_city;
        commonRights[_rightNo].rightAddress.region=_region;
        commonRights[_rightNo].rightAddress.town=_town;
        commonRights[_rightNo].rightAddress.village=_village;
        commonRights[_rightNo].rightAddress.group=_group;
    
    }

    function fillFourAddr(
        string memory _rightNo,
        string memory _fourEast,
        string memory _fourWest,
        string memory _fourSouth,
        string memory _fourNorth
    ) public {
        // 检查产权编号是否存在于映射中
        require(bytes(commonRights[_rightNo].rightNo).length != 0, "找不到产权");
        // 填入四至信息
        fourAddrs[_rightNo] = FourAddr({
            FourEast: _fourEast,
            FourWest: _fourWest,
            FourSouth: _fourSouth,
            FourNorth: _fourNorth
        });
    }

    //土地相关信息添加
    function fillLandDetails(
        string memory _rightNo,
        string memory _landNature,
        uint256 _landArea
    ) public {
        // 检查产权编号是否存在于映射中
        require(bytes(commonRights[_rightNo].rightNo).length != 0, "找不到产权");
        landRights[_rightNo].common=commonRights[_rightNo];
        // 填入土地类特有信息
        landRights[_rightNo].details.landNature = _landNature;
        landRights[_rightNo].details.landArea = _landArea;
        landRights[_rightNo].details.fourAddr=fourAddrs[_rightNo];
    }
    
    // 生产设施注册信息
    struct FacilityDetails {
        string planUse;          // 规划用途
        string facilities;       // 配套设施
        string environment;      // 周边环境
        string specification;    // 规格型号
        uint256 amount;          // 数量
        string amountUnit;       // 数量单位
        
    }
    // 房屋注册信息
    struct HouseDetails {
        uint8 landNature;        // 土地类型（1农用地 2建设用地 3未利用地）
        string landCertNo;       // 土地证号
        string estateCertNo;     // 房产证号
        string planUse;          // 规划用途
        uint256 rollOutArea;     // 建筑面积（平方米）
        uint256 landOutArea;     // 占地面积（平方米）
        string sumPeopleName;    // 共有人名称
    }
    // 知识产权注册信息
    struct IPDetails {
        string patentNo;         // 专利号或审定号
        string mainProduct;      // 主要产品
        string approvalNo;       // 审批部门文号
        uint256 applyDate;       // 权利申请日
        uint256 authDate;        // 授权公告日期
        string tradeRegAddr;     // 商标注册地
    }
    // 林地注册信息
    struct ForestDetails {
        string smallAddr;        // 小地名
        string forestClass;	 //林班
        string smallClass; //小班
        uint256 certArea;        // 证载面积（亩）
        string mainSeed;         // 主要树种
        uint8 treeNum;//株数：
        string treeSpecies;      // 林种
        uint8 ownerType;         // 权属类型（1国有 2集体 3个人）
        FourAddr fourAddr;
    }

    // 各种特有字段的结构体定义
    struct LandRight {
        CommonRight common;      // 引用共有字段
        LandDetails details;      // 土地相关信息
    }

    struct FacilityRight {
        CommonRight common;      // 引用共有字段
        FacilityDetails details;  // 设施相关信息
    }

    struct HouseRight {
        CommonRight common;      // 引用共有字段
        HouseDetails details;     // 房屋相关信息
    }

    struct IpRight {
        CommonRight common;      // 引用共有字段
        IPDetails details;        // 知识产权相关信息
    }

    struct ForestRight {
        CommonRight common;      // 引用共有字段
        ForestDetails details;    // 林地相关信息
    }
    
    struct RollHistory{
        string step;//办理环节
        uint doneTime;//办理时间
        uint doneOrgId;//办理机构标识
        uint doneUserId;//办理用户标识
        string projectCode;//交易项目编号
        uint rejectFlag;//退回标记
    }
    
    uint rollId;
    function addHistory(string memory _rightNo, string memory _step,uint _doneTime,uint _doneOrgId,uint _doneUserId,string memory projectCode,uint _rejectFlag) public {
        rollId++;
        rollHistorys[_rightNo].push(rollId);
        rollHistory[rollId]=RollHistory(_step,_doneTime,_doneOrgId,_doneUserId,projectCode,_rejectFlag);
    }
    
    function getRightHisory(string memory _rightNo) public returns(uint[] memory){
        return rollHistorys[_rightNo];
    }
    
    function getHistoryInfo(uint _rollId)public returns(string memory,uint,uint,uint,string memory,uint){
        RollHistory memory newRollHistory=rollHistory[_rollId];
        return(newRollHistory.step,newRollHistory.doneTime,newRollHistory.doneOrgId,
        newRollHistory.doneUserId,newRollHistory.projectCode,newRollHistory.rejectFlag);
    }
    
    uint[] allOrgans;
    mapping (uint=>string[])public orgRightNo;
    mapping (string=>uint[])public rollHistorys;
    mapping (uint => RollHistory) public rollHistory;
    // 映射存储不同类型的产权信息
    mapping (string=> CommonRight)public commonRights;
    mapping (string=> FourAddr)public fourAddrs;
    // mapping (string=> RightAddress)public rightAddr;
    mapping(string => LandRight) public landRights;
    mapping(string => FacilityRight) public facilityRights;
    mapping(string => HouseRight) public houseRights;
    mapping(string => IpRight) public ipRights;
    mapping(string => ForestRight) public forestRights;
    mapping(string => string[]) public MyRights;
    // 产权编号数组
    string[] public rightNos;
    // 获取我的产权
    function getMyRights(string memory _rightCardId) public returns(string[] memory){
        return MyRights[_rightCardId];
    }
    // 更新产权公共信息
    function updateCommonInfo(
        string  memory _rightNo,
        string memory _rightName,
        uint _orgId,
        string memory _rightCertCode,
        string memory _rightOwner,
        uint _userId,
        uint8 _ownerShip,
        string memory _useStartDate,
        string memory _useEndDate,
        string memory _rightCardId
    ) public {
        require(keccak256(abi.encodePacked(commonRights[_rightNo].rightNo)) == keccak256(abi.encodePacked(_rightNo)),"该产权不存在");
        addCommonInfo(_rightNo,_rightName,_orgId,_rightCertCode,_rightOwner,_userId,_ownerShip,_useStartDate,_useEndDate,_rightCardId);
    }
    // 更新产权坐落信息
    function updateRightAddress(
        string memory _rightNo,
        string memory _zcode,
        string memory _province,
        string memory _city,
        string memory _region,
        string memory _town,
        string memory _village,
        string memory _group
    ) public {
        fillRightAddress(_rightNo,_zcode,_province,_city,_region,_town,_village,_group);
    }

    // 更新产权四至信息
    function updateFourAddr(
        string memory _rightNo,
        string memory _fourEast,
        string memory _fourWest,
        string memory _fourSouth,
        string memory _fourNorth
    ) public {
        fillFourAddr(_rightNo,_fourEast,_fourWest,_fourSouth,_fourNorth);
    }

    // 更新产权公共信息
    function updateLandDetails(string memory _rightNo,
        string memory _landNature,
        uint256 _landArea) public{
        fillLandDetails(_rightNo,_landNature,_landArea);
    }

    //生产设施注册信息
    function fillFacilityDetails(
        string memory _rightNo,
        string memory _planUse,
        string memory _facilities,
        string memory _environment,
        string memory _specification,
        uint256 _amount,
        string memory _amountUnit
    ) public {
        // 检查产权编号是否存在于映射中
        require(bytes(commonRights[_rightNo].rightNo).length != 0, "找不到产权");
        facilityRights[_rightNo].common=commonRights[_rightNo];
        // 填入生产设施类特有信息
        facilityRights[_rightNo].details.planUse = _planUse;
        facilityRights[_rightNo].details.facilities = _facilities;
        facilityRights[_rightNo].details.environment = _environment;
        facilityRights[_rightNo].details.specification = _specification;
        facilityRights[_rightNo].details.amount = _amount;
        facilityRights[_rightNo].details.amountUnit = _amountUnit;
    }

    //生产设施详细信息
    function updateFacilityDetails(
        string memory _rightNo,
        string memory _planUse,
        string memory _facilities,
        string memory _environment,
        string memory _specification,
        uint256 _amount,
        string memory _amountUnit
    ) public {
        fillFacilityDetails(_rightNo,_planUse,_facilities,_environment,_specification,_amount,_amountUnit);
    }


    //房屋注册信息
    function fillHouseDetails(
        string memory _rightNo,
        uint8 _landNature,
        string memory _landCertNo,
        string memory _estateCertNo,
        string memory _planUse,
        uint256 _rollOutArea,
        uint256 _landOutArea,
        string memory _sumPeopleName
    ) public {
        // 检查产权编号是否存在于映射中
        require(bytes(commonRights[_rightNo].rightNo).length != 0, "找不到产权");
        houseRights[_rightNo].common=commonRights[_rightNo];
    
        // 填入房屋类特有信息
        houseRights[_rightNo].details.landNature = _landNature;
        houseRights[_rightNo].details.landCertNo = _landCertNo;
        houseRights[_rightNo].details.estateCertNo = _estateCertNo;
        houseRights[_rightNo].details.planUse = _planUse;
        houseRights[_rightNo].details.rollOutArea = _rollOutArea;
        houseRights[_rightNo].details.landOutArea = _landOutArea;
        houseRights[_rightNo].details.sumPeopleName = _sumPeopleName;
    }

    function updateHouseDetails(
        string memory _rightNo,
        uint8 _landNature,
        string memory _landCertNo,
        string memory _estateCertNo,
        string memory _planUse,
        uint256 _rollOutArea,
        uint256 _landOutArea,
        string memory _sumPeopleName
    ) public {
        fillHouseDetails(_rightNo,_landNature,_landCertNo,_estateCertNo,_planUse,_rollOutArea,_landOutArea,_sumPeopleName);
    }

    //知识产权相关信息
    function fillIPDetails(
        string memory _rightNo,
        string memory _patentNo,
        string memory _mainProduct,
        string memory _approvalNo,
        uint256 _applyDate,
        uint256 _authDate,
        string memory _tradeRegAddr
    ) public {
        // 检查产权编号是否存在于映射中
        require(bytes(commonRights[_rightNo].rightNo).length != 0, "找不到产权");
        ipRights[_rightNo].common=commonRights[_rightNo];
        // 填入知识产权类特有信息
        ipRights[_rightNo].details.patentNo = _patentNo;
        ipRights[_rightNo].details.mainProduct = _mainProduct;
        ipRights[_rightNo].details.approvalNo = _approvalNo;
        ipRights[_rightNo].details.applyDate = _applyDate;
        ipRights[_rightNo].details.authDate = _authDate;
        ipRights[_rightNo].details.tradeRegAddr = _tradeRegAddr;
    }

    function updateIPDetails(
        string memory _rightNo,
        string memory _patentNo,
        string memory _mainProduct,
        string memory _approvalNo,
        uint256 _applyDate,
        uint256 _authDate,
        string memory _tradeRegAddr
    ) public {
        fillIPDetails(_rightNo,_patentNo,_mainProduct,_approvalNo,_applyDate,_authDate,_tradeRegAddr);
    }

    function fillForestDetails(
        string memory _rightNo,
        string memory _smallAddr,
        string memory _forestClass,
        string memory _smallClass,
        uint256 _certArea,
        uint8 _treeNum,
        string memory _mainSeed,
        string memory _treeSpecies,
        uint8 _ownerType
    ) public {
        // 检查产权编号是否存在于映射中
       require(bytes(commonRights[_rightNo].rightNo).length != 0, "找不到产权");
        forestRights[_rightNo].common=commonRights[_rightNo];
        // 填入林地类特有信息
        forestRights[_rightNo].details.smallAddr = _smallAddr;
        forestRights[_rightNo].details.certArea = _certArea;
        forestRights[_rightNo].details.forestClass = _forestClass;
        forestRights[_rightNo].details.smallClass = _smallClass;
        forestRights[_rightNo].details.mainSeed = _mainSeed;
        forestRights[_rightNo].details.treeNum = _treeNum;
        forestRights[_rightNo].details.treeSpecies = _treeSpecies;
        forestRights[_rightNo].details.ownerType = _ownerType;
        forestRights[_rightNo].details.fourAddr=fourAddrs[_rightNo];
    }

    function updateForestDetails(
        string memory _rightNo,
        string memory _smallAddr,
        string memory _forestClass,
        string memory _smallClass,
        uint256 _certArea,
        uint8 _treeNum,
        string memory _mainSeed,
        string memory _treeSpecies,
        uint8 _ownerType
    ) public {
        fillForestDetails(_rightNo,_smallAddr,_forestClass,_smallClass,_certArea,_treeNum,_mainSeed,_treeSpecies,_ownerType);
    }

    //获得产权的公告信息
    function getRightCommon(string memory _rightNo) public returns( string memory,string memory,uint,
    string memory,string memory,uint,uint8,string memory,string memory,string memory){
       CommonRight memory newCommon=commonRights[_rightNo];
       return (newCommon.rightNo,newCommon.rightName,newCommon.orgId,
       newCommon.rightCertCode,newCommon.rightOwner,newCommon.userId,
       newCommon.ownerShip,newCommon.useStartDate,newCommon.useEndDate,newCommon.rightCardId);
    }
    
    //获得产权所在地
    function getRightAddr(string memory _rightNo) public returns(string memory,string memory,string memory,string memory,string memory,string memory,string memory){
        CommonRight memory newCommon=commonRights[_rightNo];
        return (newCommon.rightAddress.zcode,newCommon.rightAddress.province,
        newCommon.rightAddress.city,newCommon.rightAddress.region,newCommon.rightAddress.town,newCommon.rightAddress.village,newCommon.rightAddress.group);
    }

    //获得土地具体信息
    function getLandDetails(string memory _rightNo)
    public
    view
    returns (
        string memory landNature,
        uint256 landArea,
        string memory fourEast,
        string memory fourWest,
        string memory fourSouth,
        string memory fourNorth
    )
    {
        // 确保土地产权存在
        require(bytes(commonRights[_rightNo].rightNo).length != 0, "找不到产权");
        // 返回土地信息以及四至信息
        return (
            landRights[_rightNo].details.landNature,
            landRights[_rightNo].details.landArea,
            landRights[_rightNo].details.fourAddr.FourEast,
            landRights[_rightNo].details.fourAddr.FourWest,
            landRights[_rightNo].details.fourAddr.FourSouth,
            landRights[_rightNo].details.fourAddr.FourNorth
        );
    }

    function getForestDetails(string memory _rightNo)
    public
    returns (
        string memory smallAddr,
        string memory forestClass,
        string memory smallClass,
        uint256 certArea,
        uint8 treeNum,
        string memory mainSeed,
        string memory treeSpecies,
        uint8 ownerType
    )
    {
        // 确保林地产权存在
        require(bytes(commonRights[_rightNo].rightNo).length != 0, "找不到产权");
        // 将 details 提取为局部变量，减少栈深度
        ForestDetails storage details = forestRights[_rightNo].details;
        // 返回林地信息
        return (
            details.smallAddr,
            details.forestClass,
            details.smallClass,
            details.certArea,
            details.treeNum,
            details.mainSeed,
            details.treeSpecies,
            details.ownerType
        );
    }

    //获得林地四至fourAddrs的信息
    function getForestFourAddr(string memory _rightNo)
    public
    view
    returns (
        string memory fourEast,
        string memory fourWest,
        string memory fourSouth,
        string memory fourNorth
    )
    {
        // 确保林地产权存在
        require(bytes(commonRights[_rightNo].rightNo).length != 0, "找不到产权");
        // 返回四至信息
        return (
            forestRights[_rightNo].details.fourAddr.FourEast,
            forestRights[_rightNo].details.fourAddr.FourWest,
            forestRights[_rightNo].details.fourAddr.FourSouth,
            forestRights[_rightNo].details.fourAddr.FourNorth
        );
    }
    
    //获得知识产权具体信息
    function getIPDetails(string memory _rightNo)
    public
    view
    returns (
        string memory patentNo,
        string memory mainProduct,
        string memory approvalNo,
        uint256 applyDate,
        uint256 authDate,
        string memory tradeRegAddr
    )
    {
        // 确保知识产权存在
        require(bytes(commonRights[_rightNo].rightNo).length != 0, "找不到产权");
        // 返回知识产权信息
        return (
            ipRights[_rightNo].details.patentNo,
            ipRights[_rightNo].details.mainProduct,
            ipRights[_rightNo].details.approvalNo,
            ipRights[_rightNo].details.applyDate,
            ipRights[_rightNo].details.authDate,
            ipRights[_rightNo].details.tradeRegAddr
        );
    }

    //获得生产设施具体信息
    function getFacilityDetails(string memory _rightNo)
    public
    view
    returns (
        string memory planUse,
        string memory facilities,
        string memory environment,
        string memory specification,
        uint256 amount,
        string memory amountUnit
    )
    {
        // 确保生产设施产权存在
        require(bytes(commonRights[_rightNo].rightNo).length != 0, "找不到产权");
        // 返回生产设施信息
        return (
            facilityRights[_rightNo].details.planUse,
            facilityRights[_rightNo].details.facilities,
            facilityRights[_rightNo].details.environment,
            facilityRights[_rightNo].details.specification,
            facilityRights[_rightNo].details.amount,
            facilityRights[_rightNo].details.amountUnit
        );
    }

    //获得房屋具体信息
    function getHouseDetails(string memory _rightNo)
    public
    view
    returns (
        uint8 landNature,
        string memory landCertNo,
        string memory estateCertNo,
        string memory planUse,
        uint256 rollOutArea,
        uint256 landOutArea,
        string memory sumPeopleName
    )
    {
        // 确保房屋产权存在
        require(bytes(commonRights[_rightNo].rightNo).length != 0, "找不到产权");
        // 返回房屋信息
        return (
            houseRights[_rightNo].details.landNature,
            houseRights[_rightNo].details.landCertNo,
            houseRights[_rightNo].details.estateCertNo,
            houseRights[_rightNo].details.planUse,
            houseRights[_rightNo].details.rollOutArea,
            houseRights[_rightNo].details.landOutArea,
            houseRights[_rightNo].details.sumPeopleName
        );
    }
    
    function getAllOrgs() public returns(uint[] memory){
        return allOrgans;
    }
  
    function getOrgRight(uint _orgId) public returns(string[] memory){
        return orgRightNo[_orgId];
    }
}
