package com.nt.auth.service;


import com.nt.auth.domain.dto.UserDTO;
import com.nt.auth.domain.po.User;
import com.nt.common.Result;

public interface AuthService {
    Result<String> register(User user);

    User login(UserDTO user);
}
