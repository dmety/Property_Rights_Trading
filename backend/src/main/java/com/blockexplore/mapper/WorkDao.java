package com.blockexplore.mapper;

import com.blockexplore.callback.DoneToDoCallBack;
import com.blockexplore.model.History;
import com.blockexplore.utils.Result;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkDao {
    List<DoneToDoCallBack> getReviewToDoList();
    List<History> getReviewDoneList(String userId);
    List<DoneToDoCallBack> getReToDoList();
    List<History> getReDoneList(String userId);
    List<DoneToDoCallBack> getAssuranceToDoList();
    List<History> getAssuranceDoneList(String userId);
    List<DoneToDoCallBack> getConfirmToDoList();
    List<History> getConfirmDoneList(String userId);
}
