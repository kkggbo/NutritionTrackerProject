package com.nt.tracker.controller;

import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.IntakeDTO;
import com.nt.tracker.domain.dto.UserProfileDTO;
import com.nt.tracker.domain.vo.IntakeDetailVO;
import com.nt.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 设置用户信息
     *
     * @param userProfile
     * @return
     */
    @PostMapping("/profile/set")
    public Result<String> setUserProfile(@RequestBody UserProfileDTO userProfile) {
        return userService.setUserProfile(userProfile);
    }

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/profile/get/{userId}")
    public Result<UserProfileDTO> getUserProfile(@PathVariable Long userId) {
        return userService.getUserProfile(userId);
    }


}
