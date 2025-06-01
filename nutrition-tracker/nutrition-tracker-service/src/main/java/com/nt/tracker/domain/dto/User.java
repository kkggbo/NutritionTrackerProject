package com.nt.tracker.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    // 用户id
    private Long id;

    // 用户名
    private String username;

    // 密码
    private String password;
}
