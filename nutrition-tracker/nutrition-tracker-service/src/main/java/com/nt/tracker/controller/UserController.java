package com.nt.tracker.controller;

import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.IntakeDTO;
import com.nt.tracker.domain.dto.UserProfileDTO;
import com.nt.tracker.domain.vo.DiaryVO;
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
     * @return
     */
    @GetMapping("/profile/get")
    public Result<UserProfileDTO> getUserProfile() {
        return userService.getUserProfile();
    }

    /**
     * 获取用户日记页数据
     * @return
     */
    @GetMapping("/diary")
    public Result<DiaryVO> getDiary() {
        return userService.getDiary();
    }

}
