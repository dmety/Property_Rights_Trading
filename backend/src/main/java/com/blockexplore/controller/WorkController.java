package com.blockexplore.controller;

import com.blockexplore.callback.DoneToDoCallBack;
import com.blockexplore.mapper.WorkDao;
import com.blockexplore.model.History;
import com.blockexplore.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/work")
public class WorkController {
    @Autowired
    WorkDao workDao;
    @RequestMapping("/getReviewToDoList")
    public Result<List<DoneToDoCallBack>> getReviewToDoList(){
        return Result.success(workDao.getReviewToDoList());
    }
    @RequestMapping("/getReviewDoneList")
    public Result<List<History>> getReviewDoneList(@RequestParam("userId") String userId){
        return Result.success(workDao.getReviewDoneList(userId));
    }
    @RequestMapping("/getConfirmToDoList")
    public Result<List<DoneToDoCallBack>> getConfirmToDoList(){
        return Result.success(workDao.getConfirmToDoList());
    }
    @RequestMapping("/getConfirmDoneList")
    public Result<List<History>> getConfirmDoneList(@RequestParam("userId") String userId){
        return Result.success(workDao.getConfirmDoneList(userId));
    }
    @RequestMapping("/getReToDoList")
    public Result<List<DoneToDoCallBack>> getReToDoList(){
        return Result.success(workDao.getReToDoList());
    }
    @RequestMapping("/getReDoneList")
    public Result<List<History>> getReDoneList(@RequestParam("userId") String userId){
        return Result.success(workDao.getReDoneList(userId));
    }
    @RequestMapping("/getAssuranceToDoList")
    public Result<List<DoneToDoCallBack>> getAssuranceToDoList(){
        return Result.success(workDao.getAssuranceToDoList());
    }
    @RequestMapping("/getAssuranceDoneList")
    public Result<List<History>> getAssuranceDoneList(@RequestParam("userId") String userId){
        return Result.success(workDao.getAssuranceDoneList(userId));
    }
}
