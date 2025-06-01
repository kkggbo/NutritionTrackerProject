package com.nt.tracker.service;

import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.UserProfileDTO;
import com.nt.tracker.domain.vo.DiaryVO;

public interface UserService {
    Result<String> setUserProfile(UserProfileDTO userProfile);

    Result<UserProfileDTO> getUserProfile();

    Result<DiaryVO> getDiary();
}
