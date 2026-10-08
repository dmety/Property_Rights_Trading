package com.blockexplore.mapper;

import com.blockexplore.model.Organ;
import org.apache.ibatis.annotations.Mapper;
import org.fisco.bcos.sdk.abi.datatypes.Bool;

import java.util.List;

@Mapper
public interface OrganDao {
    Boolean createOrgan(Organ organ);
    Boolean isOrganExist(String orgCode);

    List<String> getAllOrganId();

    Boolean deleteOrgan(String orgId);

    List<Organ> getAllOrganByType(String transKind);
}
