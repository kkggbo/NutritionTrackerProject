package com.nt.user.controller;


import com.nt.common.Result;
import com.nt.user.domain.dto.UserProfileDTO;
import com.nt.user.domain.vo.DiaryVO;
import com.nt.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    // Todo 把功能转移到food service服务
    /**
     * 获取用户日记页数据
     * @return
     */
    @GetMapping("/diary")
    public Result<DiaryVO> getDiary() {
        return userService.getDiary();
    }

}
