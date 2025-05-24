package com.nt.tracker.controller;

import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.UserProfileDTO;
import com.nt.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/profile/set")
    public Result<String> setUserProfile(@RequestBody UserProfileDTO userProfile) {
        return userService.setUserProfile(userProfile);
    }

    @GetMapping("/profile/get/{userId}")
    public Result<UserProfileDTO> getUserProfile(@PathVariable Long userId) {
        return userService.getUserProfile(userId);
    }

}
