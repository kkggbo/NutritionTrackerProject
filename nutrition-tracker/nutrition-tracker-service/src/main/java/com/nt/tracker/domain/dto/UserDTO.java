package com.nt.tracker.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {

//    // 用户id
//    private int id;

    // 用户名
    private String username;

    // 密码
    private String password;
}
