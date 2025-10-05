package com.nt.user.service;

import com.nt.common.Result;
import com.nt.user.domain.po.UserChallenge;
import com.nt.user.domain.vo.ChallengeVO;
import com.nt.user.domain.vo.UserChallengeVO;


import java.util.List;

public interface UserChallengeService {

    Result<String> startChallenge(Long challengeId);

    Result<String> terminateChallenge(Long id);

    Result<List<ChallengeVO>> getAllActiveChallenges();

    Result<List<UserChallengeVO>> getUserChallenges();
}
