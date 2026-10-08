pragma experimental ABIEncoderV2;
pragma solidity >=0.4.24 < 0.6.11;
import './Right.sol';

contract ProjectTrade{
    
    address rightAddr;
    constructor(address _rightAddr) public  {
        rightAddr=_rightAddr;
    }
    //项目信息
    struct ProjectInfo{
        string projectCode;
        string rightNo;          // 产权编号
        string transKind;//交易类别：
        string[] transferee;
        uint userId;//受理登记人代码
        uint organId;//受理机构代码：	
   	    string aptDate;//受理登记时间
   	    string projectStartDate;
        string projectEndDate;
        string projectStatus;
        uint successPulic;//成交公告
        string area;//项目的面积
        string perimeter;//项目的周长
    }
    mapping(string=> UpInfo) upInfos;
    mapping (string=> ContractInfo) contractInfos;
    mapping(string=>AppraisalCert) appraisalCerts;
    mapping(string=>Transferor) transferors;
    
    //采集点信息
    struct PointInfo{
        string projectCode;//当前采集点所属项目
        string pointName;//采集点名称
        string dataName;//数据名称
        string dataValue;//数据值
        uint deviceId;//当前采集点所属设备id
        string collectTime;//采集时间
    }
    // 项目编号 => 采集点名称 => 采集点信息
    mapping(string => mapping(string => PointInfo)) pointInfos;

    // 项目编号 => 该项目下所有采集点名称列表
    mapping(string => string[]) projectPoints;

   //挂牌信息
    struct UpInfo{
        string projectCode;
        string upStatus;//当前挂牌状态
        string upStartDate;//挂牌起始日期
        string upEndDate;//挂牌结束日期
        uint upPrice;//挂牌价格
        string upPriceUnit;//挂牌单位    元   元/每亩/每年
        string rollOutMode;//拟流转方式：1转让 2转包 3出租
        uint rollOutArea;//拟流转面积（亩）：
        uint isPubUp;//是否发布挂牌信息 0 否 1 是
    }
    
       //转出方
    struct Transferor{
        string userType;//主体类型
        string transferorName;
        string cardType;//证件类型
        string transferorCardNo;//身份证号
        string organRegNo;//社会统一信用代码
        uint telephone;//	联系电话
        string Email;//		电子邮件
        string condition;		//受让方应有条件
        string transMode;//交易方式
    }
    //合同
    struct ContractInfo{
        string contractCode;//合同编号
        uint successPrice;//合同成交金额：
        string successPriceUnit;//金额单位 0元  1元/每亩/每年
        uint sumSuccessPrice;//合同成交总金额（元）：
        string contractDate;//合同签署日期：
        string contractRollOutMode;//合同流转方式：（转让 转包 出租）
        string contractRollOutStartDate;//合同流转期限起始：
        string contractRollOutEndDate;//合同流转期限终止：
        uint contractRollOutArea;//合同流转面积数：
    }
    //鉴证书结构体
    struct AppraisalCert{
        string appraisalCertNo;//鉴证编号
        string appraisalData;//鉴证信息
    }

 
    //受让方
    struct Transferee{
        string transfereeName;
        string cardType;//		证件类型
        string transfereeCardNo;//身份证
        string organRegNo;//社会统一信用代码
        uint telephone;//	联系电话
        string Email;//		电子邮件
        uint willPrice;//		意向价格
        string willPriceUnit;//	意向价格单位		
        uint status;//		受让状态（1确定为受让方  0未确定受让方）
        string userType;//主体类型
    }
    

    mapping (string =>ProjectInfo) projectInfoes;
    mapping (string => uint[]) reviewArr;//各个审核确认环节 [1前置审批，2转出审核，3受让审核，4合同审核]
    mapping (uint => string[]) stepArr;//各个环节的项目编号 1 处于前置审批 2.。。。。
    mapping (string => Transferee) transfereeMap;
    mapping (string =>string[]) myProjects;//sfz 我的项目
    
    //新增采集点
    function addPoint(string memory _pointName,string memory _projectCode) public{
        if (bytes(pointInfos[_projectCode][_pointName].pointName).length == 0) {
            projectPoints[_projectCode].push(_pointName);
        }
        pointInfos[_projectCode][_pointName] = PointInfo({
            projectCode: _projectCode,
            pointName: _pointName,
            dataName: "",
            dataValue: "",
            deviceId: 0,
            collectTime: ""
        });
    }
    
    function updatePoint(
        string memory _projectCode,
        string memory _pointName,
        string memory _dataName,
        string memory _dataValue,
        uint _deviceId,
        string memory _collectTime
    ) public {
        // 获取当前采集点信息
        PointInfo storage point = pointInfos[_projectCode][_pointName];
    
        // 判断该采集点是否存在（根据 pointName 是否为空）
        if (bytes(point.pointName).length == 0) {
            revert("Point does not exist.");
        }
    
        // 更新 dataName 如果当前是空值
        if (bytes(point.dataName).length == 0 && bytes(_dataName).length > 0) {
            point.dataName = _dataName;
        }
    
        // 更新 dataValue 如果当前是空值
        if (bytes(point.dataValue).length == 0 && bytes(_dataValue).length > 0) {
            point.dataValue = _dataValue;
        }
    
        // 更新 deviceId 如果当前为 0 且参数不为 0
        if (point.deviceId == 0 && _deviceId != 0) {
            point.deviceId = _deviceId;
        }
    
        // 更新 collectTime 如果当前是空值
        if (bytes(point.collectTime).length == 0 && bytes(_collectTime).length > 0) {
            point.collectTime = _collectTime;
        }
    }
    
    // 删除指定项目的采集点
    function delPoint(string memory _projectCode, string memory _pointName) public {
        // 检查采集点是否存在
        require(
            bytes(pointInfos[_projectCode][_pointName].pointName).length != 0,
            "采集点不存在"
        );
        
        // 删除采集点信息
        delete pointInfos[_projectCode][_pointName];
        
        // 从项目采集点列表中移除
        string[] storage points = projectPoints[_projectCode];
        for (uint i = 0; i < points.length; i++) {
            if (keccak256(abi.encodePacked(points[i])) == keccak256(abi.encodePacked(_pointName))) {
                // 将匹配元素与最后一个元素交换后删除
                if (i < points.length - 1) {
                    points[i] = points[points.length - 1];
                }
                points.pop();
                break;
            }
    }
    
    // 可选的: 添加操作日志
    // Right(rightAddr).addHistory(...);
}


    // 获取指定项目的单个采集点信息
    function getPoint(
        string memory _projectCode, 
        string memory _pointName
    ) public view returns (
        string memory projectCode,
        string memory pointName,
        string memory dataName,
        string memory dataValue,
        uint deviceId,
        string memory collectTime
    ) {
        // 获取采集点信息
        PointInfo storage point = pointInfos[_projectCode][_pointName];
        
        // 检查采集点是否存在
        require(
            bytes(point.pointName).length != 0,
            "采集点不存在"
        );
        
        // 返回采集点信息
        return (
            point.projectCode,
            point.pointName,
            point.dataName,
            point.dataValue,
            point.deviceId,
            point.collectTime
        );
    }
   
    function getAllPoints(string memory _projectCode) public view returns (
        string[] memory projectCodes,
        string[] memory pointNames,
        string[] memory dataNames,
        string[] memory dataValues,
        uint[] memory deviceIds,
        string[] memory collectTimes
    ) {
        uint length = projectPoints[_projectCode].length;
    
        projectCodes = new string[](length);
        pointNames = new string[](length);
        dataNames = new string[](length);
        dataValues = new string[](length);
        deviceIds = new uint[](length);
        collectTimes = new string[](length);
    
        for (uint i = 0; i < length; i++) {
            string memory pointName = projectPoints[_projectCode][i];
            PointInfo storage info = pointInfos[_projectCode][pointName];
    
            projectCodes[i] = info.projectCode;  // 或 projectCodes[i] = _projectCode;
            pointNames[i] = info.pointName;
            dataNames[i] = info.dataName;
            dataValues[i] = info.dataValue;
            deviceIds[i] = info.deviceId;
            collectTimes[i] = info.collectTime;
        }
    }

            
    // 创建项目
    function createProject(
        string memory _projectCode,
        string memory _rightNo,
        string memory _transKind,
        uint _userId,
        uint _organId,
        string memory _aptDate,
        string memory _projectStartDate,
        string memory _projectEndDate
    ) public {
        //结构体赋值基本信息
        projectInfoes[_projectCode].projectCode=_projectCode;
        projectInfoes[_projectCode].rightNo=_rightNo;
        projectInfoes[_projectCode].transKind=_transKind;
        projectInfoes[_projectCode].userId=_userId;
        projectInfoes[_projectCode].organId=_organId;
        projectInfoes[_projectCode].aptDate=_aptDate;
        projectInfoes[_projectCode].projectStartDate=_projectStartDate;
        projectInfoes[_projectCode].projectEndDate=_projectEndDate;
        projectInfoes[_projectCode].projectStatus='前置审批';
        projectInfoes[_projectCode].area="";
        projectInfoes[_projectCode].perimeter="";
        stepArr[1].push(_projectCode);
        Right(rightAddr).addHistory(projectInfoes[_projectCode].rightNo,"项目受理",block.timestamp,projectInfoes[_projectCode].organId,projectInfoes[_projectCode].userId,_projectCode,1);
        
    }
    //更新项目的周长面积
    function updateAP(string memory _ProjectCode,string memory _area,string memory _perimeter) public{
        // 先检查项目是否存在，判断projectCode是否为空
        require(bytes(projectInfoes[_ProjectCode].projectCode).length != 0, "Project not exist");

        // 更新area和perimeter
        projectInfoes[_ProjectCode].area = _area;
        projectInfoes[_ProjectCode].perimeter = _perimeter;
    }
    
     function queryAP(string memory _ProjectCode) public view returns(string memory area,string memory perimeter){
        string memory a = projectInfoes[_ProjectCode].area;
        string memory p = projectInfoes[_ProjectCode].perimeter;
         return (a,p);
    }
    
        
    //前置审批
    function frontRe(string memory _ProjectCode, uint _userId) public {
        // 遍历找到要删除的元素
        for (uint i = 0; i < stepArr[1].length; i++) {
            if (keccak256(abi.encodePacked(stepArr[1][i])) == keccak256(abi.encodePacked(_ProjectCode))) {
                // 用最后一个元素替换要删除的元素
                stepArr[1][i] = stepArr[1][stepArr[1].length - 1];
                // 移除最后一个元素
                stepArr[1].pop();
                break; // 退出循环，避免多次删除
            }
        }

        projectInfoes[_ProjectCode].projectStatus='转出审核';
        reviewArr[_ProjectCode].push(1);
        stepArr[2].push(_ProjectCode);
        Right(rightAddr).addHistory(projectInfoes[_ProjectCode].rightNo, "前置审批", block.timestamp, projectInfoes[_ProjectCode].organId, _userId, _ProjectCode, 1);
    }
    
    function reBack(string memory _ProjectCode,uint _nowStep,uint _step,uint _userId,string memory _projectStatus)public{
        stepArr[_step].push(_ProjectCode);
        for(uint i=0;i<stepArr[_nowStep].length;i++){
            if (keccak256(abi.encodePacked(stepArr[_nowStep][i]))== keccak256(abi.encodePacked(_ProjectCode))){
                // 将要删除的元素替换为最后一个元素
                stepArr[_nowStep][i] = stepArr[_nowStep][stepArr[_nowStep].length - 1];
                // 移除最后一个元素
                stepArr[_nowStep].pop(); 
                break; // 退出循环，避免多次删除
            }
        }
        projectInfoes[_ProjectCode].projectStatus=_projectStatus;
        Right(rightAddr).addHistory(projectInfoes[_ProjectCode].rightNo,"驳回",block.timestamp,projectInfoes[_ProjectCode].organId,_userId,_ProjectCode,0);
    }
    
    //转出审批
    function transferRe(string memory _ProjectCode,uint _userId) public{
        for(uint i=0;i<stepArr[2].length;i++){
            if (keccak256(abi.encodePacked(stepArr[2][i]))== keccak256(abi.encodePacked(_ProjectCode))){
                // 将要删除的元素替换为最后一个元素
                stepArr[2][i] = stepArr[2][stepArr[2].length - 1];
                // 移除最后一个元素
                stepArr[2].pop(); 
                break; // 退出循环，避免多次删除
            }
        }
        projectInfoes[_ProjectCode].projectStatus='受让受理';
        //添加判断
        reviewArr[_ProjectCode].push(2);
        stepArr[3].push(_ProjectCode);
        Right(rightAddr).addHistory(projectInfoes[_ProjectCode].rightNo,"转出审核",block.timestamp,projectInfoes[_ProjectCode].organId,_userId,_ProjectCode,1);
    }

    //受让审核
    function tradeRe(string memory _ProjectCode,uint _userId) public{
        for(uint i=0;i<stepArr[3].length;i++){
            if (keccak256(abi.encodePacked(stepArr[3][i]))== keccak256(abi.encodePacked(_ProjectCode))){
                // 将要删除的元素替换为最后一个元素
                stepArr[3][i] = stepArr[3][stepArr[3].length - 1];
                // 移除最后一个元素
                stepArr[3].pop(); 
                break; // 退出循环，避免多次删除
            }
        }
        //添加判断
        projectInfoes[_ProjectCode].projectStatus='合同签订';
        reviewArr[_ProjectCode].push(3);
        stepArr[4].push(_ProjectCode);
        Right(rightAddr).addHistory(projectInfoes[_ProjectCode].rightNo,"受让审核",block.timestamp,projectInfoes[_ProjectCode].organId,_userId,_ProjectCode,1);
    }

    //合同审核
    function contractRe(string memory _ProjectCode,uint _userId)public{
        for(uint i=0;i<stepArr[4].length;i++){
            if (keccak256(abi.encodePacked(stepArr[4][i]))== keccak256(abi.encodePacked(_ProjectCode))){
                // 将要删除的元素替换为最后一个元素
                stepArr[4][i] = stepArr[4][stepArr[4].length - 1];
                // 移除最后一个元素
                stepArr[4].pop(); 
                break; // 退出循环，避免多次删除
            }
        }
        //添加判断
        projectInfoes[_ProjectCode].projectStatus='鉴证打印';
        reviewArr[_ProjectCode].push(4);
        stepArr[5].push(_ProjectCode);
        Right(rightAddr).addHistory(projectInfoes[_ProjectCode].rightNo,"合同审核",block.timestamp,projectInfoes[_ProjectCode].organId,_userId,_ProjectCode,1);
    }
    
    function getFronStep() public returns(string[] memory){
            return stepArr[1];
    }
    
    function getTransStep() public returns(string[] memory){
        return stepArr[2];
    }
        
    function getTradeStep() public returns(string[] memory){
        return stepArr[3];
    }
        
    function getContractStep() public returns(string[] memory){
        return stepArr[4];
    }
        
    function getEndStep() public returns(string[] memory){
        return stepArr[5];
    }
    
    function upadteProject(
        string memory _projectCode,
        string memory _rightNo,
        string memory _transKind,
        uint _userId,
        uint _organId,
        string memory _aptDate,
        string memory _projectStartDate,
        string memory _projectEndDate
    ) public {
        upInfos[_projectCode].projectCode=_projectCode;
        projectInfoes[_projectCode].rightNo=_rightNo;
        projectInfoes[_projectCode].transKind=_transKind;
        projectInfoes[_projectCode].userId=_userId;
        projectInfoes[_projectCode].organId=_organId;
        projectInfoes[_projectCode].aptDate=_aptDate;
        projectInfoes[_projectCode].projectStartDate=_projectStartDate;
        projectInfoes[_projectCode].projectEndDate=_projectEndDate;
         Right(rightAddr).addHistory(projectInfoes[_projectCode].rightNo,"项目更新",block.timestamp,projectInfoes[_projectCode].organId,projectInfoes[_projectCode].userId,_projectCode,1);
    }

    // 添加挂牌信息
    function addUpInfo(
        string memory _projectCode,
        string memory _upStatus,
        string memory _upStartDate,
        string memory _upEndDate,
        uint _upPrice,
        string memory _upPriceUnit,
        string memory _rollOutMode,
        uint _rollOutArea,
        uint _userId
    ) public {
        upInfos[_projectCode] = UpInfo(
            _projectCode,
            _upStatus,
            _upStartDate,
            _upEndDate,
            _upPrice,
            _upPriceUnit,
            _rollOutMode,
            _rollOutArea,
            0
        );
    }
    
    // 添加挂牌信息
    function updateUpInfo(
        string memory _projectCode,
        string memory _upStatus,
        string memory _upStartDate,
        string memory _upEndDate,
        uint _upPrice,
        string memory _upPriceUnit,
        string memory _rollOutMode,
        uint _rollOutArea,
        uint _userId
    ) public {
        upInfos[_projectCode] = UpInfo(
            _projectCode,
            _upStatus,
            _upStartDate,
            _upEndDate,
            _upPrice,
            _upPriceUnit,
            _rollOutMode,
            _rollOutArea,
            0
        );
        Right(rightAddr).addHistory(projectInfoes[_projectCode].rightNo,"挂牌申请修改",block.timestamp,projectInfoes[_projectCode].organId,_userId,_projectCode,1);
    }
    
    //发布挂牌
    function goUpInfo(string memory _projectCode,uint _userId) public{
        upInfos[_projectCode].upStatus='已挂牌';
        upInfos[_projectCode].isPubUp=1;
        projectInfoes[_projectCode].projectStatus='受让受理';
        Right(rightAddr).addHistory(projectInfoes[_projectCode].rightNo,"挂牌发布",block.timestamp,projectInfoes[_projectCode].organId,_userId,_projectCode,1);
    }
    
    // 延牌方法
    function extendUpEndDate(string memory _projectCode, string memory _newEndDate,uint _userId) public {
        upInfos[_projectCode].upEndDate = _newEndDate;
        // projectInfoes[_projectCode].projectStatus='延牌';
        Right(rightAddr).addHistory(projectInfoes[_projectCode].rightNo,"延牌",block.timestamp,projectInfoes[_projectCode].organId,_userId,_projectCode,1);
    }

    // 摘牌方法
    function delistProject(string memory _projectCode,uint _userId) public {
        upInfos[_projectCode].upStatus='已摘牌';
        upInfos[_projectCode].isPubUp = 0;
        // projectInfoes[_projectCode].projectStatus='已摘牌';
        Right(rightAddr).addHistory(projectInfoes[_projectCode].rightNo,"摘牌",block.timestamp,projectInfoes[_projectCode].organId,_userId,_projectCode,1);
    }
    
    function backUp(string memory _projectCode,uint _userId) public{
         upInfos[_projectCode].upStatus='已撤牌';
        upInfos[_projectCode].isPubUp = 0;
        // projectInfoes[_projectCode].projectStatus='已撤牌';
        Right(rightAddr).addHistory(projectInfoes[_projectCode].rightNo,"撤牌",block.timestamp,projectInfoes[_projectCode].organId,_userId,_projectCode,1);
    }
    
    //发布成交公告
    function goSuPu(string memory _projectCode,uint _userId) public{
        projectInfoes[_projectCode].successPulic=1;
        // projectInfoes[_projectCode].projectStatus='成交公告发布';
        Right(rightAddr).addHistory(projectInfoes[_projectCode].rightNo,"发布成交公告",block.timestamp,projectInfoes[_projectCode].organId,_userId,_projectCode,1);
    }

    // 添加转出方信息
    function addTransferor(
        string memory _userType,
        string memory _projectCode,
        string memory _transferorName,
        string memory _cardType,
        string memory _transferorCardNo,
        string memory _organRegNo,
        uint _telephone,
        string memory _Email,
        string memory _condition,
        string memory _transMode
    ) public {
        myProjects[_transferorCardNo].push(_projectCode);
        transferors[_projectCode] = Transferor(
            _userType,
            _transferorName,
            _cardType,
            _transferorCardNo,
            _organRegNo,
            _telephone,
            _Email,
            _condition,
            _transMode
        );
    }
    
    function updateTransferor(
        string memory _usertype,
        string memory _projectCode,
        string memory _transferorName,
        string memory _cardType,
        string memory _transferorCardNo,
        string memory _organRegNo,
        uint _telephone,
        string memory _Email,
        string memory _condition,
        string memory _transMode
    ) public {
        transferors[_projectCode] = Transferor(
             _usertype,
            _transferorName,
            _cardType,
            _transferorCardNo,
            _organRegNo,
            _telephone,
            _Email,
            _condition,
            _transMode
        );
    }

    // 添加受让方信息
    function addTransferee(
        string memory _projectCode,
        string memory _transfereeName,
        string memory _cardType,
        string memory _transfereeCardNo,
        string memory _organRegNo,
        uint _telephone,
        string memory _Email,
        uint _willPrice,
        string memory _willPriceUnit,
        string memory _userType
    ) public {
        transfereeMap[_transfereeCardNo] = Transferee(
            _transfereeName,
            _cardType,
            _transfereeCardNo,
            _organRegNo,
            _telephone,
            _Email,
            _willPrice,
            _willPriceUnit,
            0,
            _userType
        );
        projectInfoes[_projectCode].transferee.push(_transfereeCardNo);
    }
    
    // 更新受让方信息
    function updateTransferee(
        string memory _projectCode,
        string memory _transfereeName,
        string memory _cardType,
        string memory _transfereeCardNo,
        string memory _organRegNo,
        uint _telephone,
        string memory _Email,
        uint _willPrice,
        string memory _willPriceUnit,
        string memory _userType
    ) public {
        transfereeMap[_transfereeCardNo] = Transferee(
            _transfereeName,
            _cardType,
            _transfereeCardNo,
            _organRegNo,
            _telephone,
            _Email,
            _willPrice,
            _willPriceUnit,
            0,
            _userType
        );
    }
   	
   	// 确认受让方
    function confirmTransferee(string memory _projectCode, string memory _transfereeCardNo,uint _userId) public {
        string[] memory transferees = projectInfoes[_projectCode].transferee;
        for (uint i = 0; i < transferees.length; i++) {
            if (
                keccak256(abi.encodePacked(transfereeMap[transferees[i]].transfereeCardNo)) ==
                keccak256(abi.encodePacked(_transfereeCardNo))
            ) {
                transfereeMap[transferees[i]].status = 1;
                myProjects[transfereeMap[transferees[i]].transfereeCardNo].push(_projectCode);
            }
            else {
                transfereeMap[transferees[i]].status = 0;
            }
        }
        projectInfoes[_projectCode].projectStatus='受让审核';
          Right(rightAddr).addHistory(projectInfoes[_projectCode].rightNo,"确认受让方",block.timestamp,projectInfoes[_projectCode].organId,_userId,_projectCode,1);
    }
   	
   	// 添加合同信息
    function addContractInfo(
        string memory _projectCode,
        string memory _contractCode,
        uint _successPrice,
        string memory _successPriceUnit,
        uint _sumSuccessPrice,
        string memory _contractDate,
        string memory _contractRollOutMode,
        string memory _contractRollOutStartDate,
        string memory _contractRollOutEndDate,
        uint _contractRollOutArea
    ) public {
        contractInfos[_projectCode]= ContractInfo(
            _contractCode,
            _successPrice,
            _successPriceUnit,
            _sumSuccessPrice,
            _contractDate,
            _contractRollOutMode,
            _contractRollOutStartDate,
            _contractRollOutEndDate,
            _contractRollOutArea
        );
        projectInfoes[_projectCode].projectStatus='合同审核';
          Right(rightAddr).addHistory(projectInfoes[_projectCode].rightNo,"签订合同",block.timestamp,projectInfoes[_projectCode].organId,projectInfoes[_projectCode].userId,_projectCode,1);
    }
    
    function updateContractInfo(
        string memory _projectCode,
        string memory _contractCode,
        uint _successPrice,
        string memory _successPriceUnit,
        uint _sumSuccessPrice,
        string memory _contractDate,
        string memory _contractRollOutMode,
        string memory _contractRollOutStartDate,
        string memory _contractRollOutEndDate,
        uint _contractRollOutArea
    ) public {
        contractInfos[_projectCode] = ContractInfo(
            _contractCode,
            _successPrice,
            _successPriceUnit,
            _sumSuccessPrice,
            _contractDate,
            _contractRollOutMode,
            _contractRollOutStartDate,
            _contractRollOutEndDate,
            _contractRollOutArea
        );
        Right(rightAddr).addHistory(projectInfoes[_projectCode].rightNo,"签订合同",block.timestamp,projectInfoes[_projectCode].organId,projectInfoes[_projectCode].userId,_projectCode,1);
    }

   	//向项目中添加鉴证书信息 AppraisalCert
   	function addAppraisalCertInfo(string memory _projectCode,string memory _appraisalCertNo ,string memory _appraisalData,uint _userId) public{
   	    appraisalCerts[_projectCode]=AppraisalCert(_appraisalCertNo,_appraisalData);
   	    projectInfoes[_projectCode].projectStatus='鉴证打印完成';
   	      Right(rightAddr).addHistory(projectInfoes[_projectCode].rightNo,"出具鉴证",block.timestamp,projectInfoes[_projectCode].organId,_userId,_projectCode,1);
   	}
   	
    // 获取项目基本信息字段
    function getProjectBaseInfo(string memory _projectCode) public view returns (
        string memory projectCode,
        string memory rightNo,
        string memory transKind,
        uint aptUserCode,
        uint aptOrganCode,
        string memory aptDate,
        string  memory projectStartDate,
        string memory projectEndDate,
        string memory projectStatus,
        uint  successPulic,//成交公告
        string memory area,
        string memory perimeter
    ) {
        ProjectInfo memory p = projectInfoes[_projectCode];
        return (
            p.projectCode,
            p.rightNo,
            p.transKind,
            p.userId,
            p.organId,
            p.aptDate,
            p.projectStartDate,
            p.projectEndDate,
            p.projectStatus,
            p.successPulic,
            p.area,
            p.perimeter
        );
    }

    // 获取挂牌信息字段
    function getUpInfoFields(string memory _projectCode) public view returns (
        string memory projectCode,
        string memory upStatus,
        string memory upStartDate,
        string memory upEndDate,
        uint upPrice,
        string memory upPriceUnit,
        string memory rollOutMode,
        uint rollOutArea,
        uint isPubUp
    ) {
        UpInfo memory up = upInfos[_projectCode];
        return (
            up.projectCode,
            up.upStatus,
            up.upStartDate,
            up.upEndDate,
            up.upPrice,
            up.upPriceUnit,
            up.rollOutMode,
            up.rollOutArea,
            up.isPubUp
        );
    }

    // 获取合同信息字段
    function getContractInfoFields(string memory _projectCode) public view returns (
        string memory contractCode,
        uint successPrice,
        string memory successPriceUnit,
        uint sumSuccessPrice,
        string memory contractDate,
        string memory contractRollOutMode,
        string memory contractRollOutStartDate,
        string memory contractRollOutEndDate,
        uint contractRollOutArea
    ) {
        ContractInfo memory c = contractInfos[_projectCode];
        return (
            c.contractCode,
            c.successPrice,
            c.successPriceUnit,
            c.sumSuccessPrice,
            c.contractDate,
            c.contractRollOutMode,
            c.contractRollOutStartDate,
            c.contractRollOutEndDate,
            c.contractRollOutArea
        );
    }

    // 获取转出方信息字段
    function getTransferorFields(string memory _projectCode) public view returns (
        string memory  _usertype,
        string memory transferorName,
        string memory cardType,
        string memory transferorCardNo,
        string memory organRegNo,
        uint telephone,
        string memory Email,
        string memory condition,
        string memory _transMode
    ) {
        Transferor memory t = transferors[_projectCode];
        return (
            t.userType,
            t.transferorName,
            t.cardType,
            t.transferorCardNo,
            t.organRegNo,
            t.telephone,
            t.Email,
            t.condition,
            t.transMode
        );
    }
    
    //获得所有受让方地址
    function getTransfereeAddr(string memory _projectCode) public view returns(string[] memory){
         return projectInfoes[_projectCode].transferee;
    }

    // 获取受让方信息字段
    function getTransfereeFields(string memory _transfereeCardNo) public view returns (
        string memory transfereeName,
        string memory cardType,
        string memory transfereeCardNo,
        string memory organRegNo,
        uint telephone,
        string memory Email,
        uint willPrice,
        string memory willPriceUnit,
        uint status,
        string memory userType
    ) {
        Transferee memory t = transfereeMap[_transfereeCardNo];
        return (
            t.transfereeName,
            t.cardType,
            t.transfereeCardNo,
            t.organRegNo,
            t.telephone,
            t.Email,
            t.willPrice,
            t.willPriceUnit,
            t.status,
            t.userType
        );
    }
   	
   	//获得项目的鉴证书信息
   	function getAppraisalCertFields(string memory _projectCode) public view returns (
        string memory AppraisalCertNo,
        string memory AppraisalData
    ) {
        AppraisalCert memory ac = appraisalCerts[_projectCode];
        return (
            ac.appraisalCertNo,
            ac.appraisalData
        );
    }
    
    function getMyProject(string memory _cardId) public returns(string[] memory){
        return myProjects[_cardId];
    }
}