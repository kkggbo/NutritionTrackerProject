package com.nt.user.service;

import com.nt.user.domain.po.UserChallenge;


import java.util.List;

public interface UserChallengeService {

    UserChallenge startChallenge(UserChallenge challenge);

    List<UserChallenge> getChallengesByUser(Long userId);

    UserChallenge getChallengeDetail(Long id);

    UserChallenge terminateChallenge(Long id);
}
