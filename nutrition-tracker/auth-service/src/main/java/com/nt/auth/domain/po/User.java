package com.nt.auth.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    // 用户id
    private Long id;

    // 用户名
    private String username;

    // 密码
    private String password;

    // 前端 "记住我" 复选框
    private Boolean rememberMe = false;
}
