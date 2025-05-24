package com.nt.tracker.service;

import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.UserProfileDTO;

public interface UserService {
    Result<String> setUserProfile(UserProfileDTO userProfile);

    Result<UserProfileDTO> getUserProfile(Long userId);
}
