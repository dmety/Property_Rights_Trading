package com.blockexplore.service;

import com.blockexplore.model.ContractOrgan;
import com.blockexplore.model.Organ;
import com.blockexplore.utils.Result;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrganService {
    Result<String> createOrgan(Organ organ) throws ABICodecException;

    Result<List<ContractOrgan>> getOrganList() throws ABICodecException;

    Result<String> deleteOrgan(String orgId);

    Result<ContractOrgan> getOrganInfoById(String orgId) throws ABICodecException;

    Result<List<Organ>> getAllOrganByType(String transKind);
}
