package com.nt.user.service;


import com.nt.common.Result;
import com.nt.user.domain.dto.UserProfileDTO;
import com.nt.user.domain.vo.DiaryVO;

public interface UserService {
    Result<String> setUserProfile(UserProfileDTO userProfile);

    Result<UserProfileDTO> getUserProfile();

    Result<DiaryVO> getDiary();

    Result<String> updateUserProfile(UserProfileDTO userProfile);

    boolean deductPoints(Long userId, Integer points);

    Result<Integer> getPoints();
}
