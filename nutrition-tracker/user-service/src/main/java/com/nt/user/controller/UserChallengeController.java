package com.nt.user.controller;

import com.nt.common.Result;
import com.nt.user.domain.po.UserChallenge;
import com.nt.user.domain.vo.ChallengeVO;
import com.nt.user.domain.vo.UserChallengeVO;
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
     * 获取所有激活的挑战
     * @return
     */
    @GetMapping("/list")
    public Result<List<ChallengeVO>> listActiveChallenges() {
        return challengeService.getAllActiveChallenges();
    }

    /**
     * 开启一项挑战
     * @param challengeId
     * @return
     */
    @PostMapping("/start")
    public Result<String> startChallenge(@RequestParam Long challengeId) {
        return challengeService.startChallenge(challengeId);
    }

    @PostMapping("/terminate")
    public Result<String> terminateChallenge(@RequestParam Long userChallengeId) {
        return challengeService.terminateChallenge(userChallengeId);
    }

    @GetMapping("/ongoingList")
    public Result<List<UserChallengeVO>>  getUserChallenges() {
        return challengeService.getUserChallenges();
    }


}
