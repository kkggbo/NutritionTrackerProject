package com.nt.user.controller;

import com.nt.user.domain.po.UserChallenge;
import com.nt.user.service.UserChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/challenge")
public class UserChallengeController {

    @Autowired
    UserChallengeService challengeService;

    /**
     * 发起挑战
     */
    @PostMapping("/start")
    public UserChallenge startChallenge(@RequestBody UserChallenge challenge) {
        return challengeService.startChallenge(challenge);
    }

    /**
     * 查询用户的所有挑战
     */
    @GetMapping("/{userId}")
    public List<UserChallenge> getChallengesByUser(@PathVariable Long userId) {
        return challengeService.getChallengesByUser(userId);
    }

    /**
     * 查询单个挑战详情
     */
    @GetMapping("/detail/{id}")
    public UserChallenge getChallengeDetail(@PathVariable Long id) {
        return challengeService.getChallengeDetail(id);
    }

    /**
     * 终止挑战（用户主动放弃）
     */
    @PutMapping("/{id}/terminate")
    public UserChallenge terminateChallenge(@PathVariable Long id) {
        return challengeService.terminateChallenge(id);
    }

}
