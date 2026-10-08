package com.blockexplore.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@TableName("users")
public class User {
    private Long id;
    private String address;
    private String createTime;
    private String editTime;
    private String token;
    private String idCard;
    @NotEmpty
    private String loginName;
    @NotNull
    private Long organId;
    @NotEmpty
    private String role;
    @NotEmpty
    private String loginPass;
    private String email;
    private String phone;
}
