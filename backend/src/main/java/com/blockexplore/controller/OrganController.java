package com.blockexplore.controller;

import com.blockexplore.model.ContractOrgan;
import com.blockexplore.model.Organ;
import com.blockexplore.service.OrganService;
import com.blockexplore.utils.Result;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
* 处理机构（Organ）相关的区块链合约操作和管理逻辑。
* */
@RestController
@CrossOrigin
@RequestMapping("/org")
public class OrganController {
    @Autowired
    private OrganService organService;

    //  创建机构
    @PostMapping("/createOrgan")
    public Result<String> createOrgan(@RequestBody Organ organ) throws ABICodecException {
        return organService.createOrgan(organ);
    }

    //  获取机构列表
    @RequestMapping("/getOrganList")
    public Result<List<ContractOrgan>> getOrganList() throws ABICodecException {
        return organService.getOrganList();
    }

    //  删除机构
    @RequestMapping("/deleteOrgan")
    public Result<String> deleteOrgan(@RequestParam("orgId") String orgId) {
        return organService.deleteOrgan(orgId);
    }

    //  获取机构信息
    @RequestMapping("/getOrganInfoById")
    public Result<ContractOrgan> getOrganInfoById(@RequestParam("orgId") String orgId) throws ABICodecException {
        return organService.getOrganInfoById(orgId);
    }

    //  获取机构列表
    @RequestMapping("/getAllOrganByType")
    public Result<List<Organ>> getAllOrganByType(@RequestParam("transKind") String transKind) throws ABICodecException {
        return organService.getAllOrganByType(transKind);
    }
}
