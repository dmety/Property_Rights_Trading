package com.blockexplore.mapper;

import com.blockexplore.model.History;
import com.blockexplore.model.Project;
import com.blockexplore.model.android.ProjectInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectDao {
    Boolean createProject(Project project);

    String getMaxRightNo();
    String getMaxContractCode();

    List<String> getAllProjectCode();

    String getNameByCode(String projectCode);

    List<ProjectInfo> getProjectInfoByUserId(String userId);

    void saveContractCode(@Param("projectCode") String projectCode,@Param("successPrice") String successPrice,@Param("contractRollOutArea") String contractRollOutArea,  @Param("contractNo") String contractNo);

    String userNameByCode(String aptUserCode);

    String organNameByCode(String aptOrganCode);

    String getOrgNameById(String doneOrgId);

    String getUserNameById(String doneUserId);

    void changeStatus(String status, String projectCode);

    void setPubUpInit(String projectCode);

    List<String> getUpContractCode();

    List<String> getNotUpContractCode();

    void setIsPubUp(String projectCode);

    void updateIsPubUp(String projectCode);

    void deListProject(String projectCode);

    List<String> getNotPubUp();

    List<String> IsPubUp();

    void setProjectStatus(@Param("projectStatus") String projectStatus, @Param("projectCode") String projectCode);

    String getUserOrgId(String userId);

    void saveHistory(History history);

    String getMaxAppraisaCertNo();

    void saveAppraisalCertInfo(@Param("appraisalCertNo") String appraisalCertNo,@Param("projectCode") String projectCode);

    String getTypeByCode(String projectCode);

    void addTransferIdByCode(@Param("projectCode") String projectCode, @Param("transferorCardNo") String transferorCardNo);

    void addTransfereeIdByCode(@Param("projectCode") String projectCode,@Param("transfereeCardNo") String transfereeCardNo);

    List<ProjectInfo> getProjectInfoByIdNumber(String idNumber);

    int selectByCode(String code);

    int updateAP(String code, String area, String perimeter);
}
