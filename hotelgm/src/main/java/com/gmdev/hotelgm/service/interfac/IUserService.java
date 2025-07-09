package com.gmdev.hotelgm.service.interfac;

import com.gmdev.hotelgm.dto.Response;
import com.gmdev.hotelgm.dto.LoginRequest;
import com.gmdev.hotelgm.entity.User;

public interface IUserService {
    Response register(User user);
    Response login(LoginRequest loginRequest);
    Response updateUser(Long userId);
    Response getAllUser();
    Response getUserBookingHistory(String userId);
    Response deleteUser(String userId);
    Response getUserById(String userId);
    Response getMyInfo(String email);
}
