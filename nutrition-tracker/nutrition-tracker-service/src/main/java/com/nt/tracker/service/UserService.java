package com.nt.tracker.service;

import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.IntakeDTO;
import com.nt.tracker.domain.dto.UserProfileDTO;
import com.nt.tracker.domain.vo.IntakeDetailVO;

import java.time.LocalDate;

public interface UserService {
    Result<String> setUserProfile(UserProfileDTO userProfile);

    Result<UserProfileDTO> getUserProfile(Long userId);

}
