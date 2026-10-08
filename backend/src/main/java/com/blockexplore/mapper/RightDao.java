package com.blockexplore.mapper;

import com.blockexplore.model.Right;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RightDao {
    String getMaxRightNo();

    void saveRight(Right right);

    List<String> getOrganAllRightId(String organId);

    List<String> getTypeRightCodeList(@Param("transKind") String transKind, @Param("orgId") String orgId);

    List<String> getTransKindRightCodeList(String transKind);

    String getTypeByNo(String rightNo);

    String getOrgNameById(String orgId);

    String getUserNameById(String userId);

    void setPubUpSuccess(String projectCode);
}
