package com.nt.tracker.service;

import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.User;
import com.nt.tracker.domain.dto.UserDTO;

public interface AuthService {
    Result<String> register(String username, String password);

    User login(UserDTO user);
}
