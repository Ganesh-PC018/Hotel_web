package com.gmdev.hotelgm.Controller;

import com.gmdev.hotelgm.dto.Response;
import com.gmdev.hotelgm.entity.User;
import com.gmdev.hotelgm.service.interfac.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> getAllUser(){
        Response response = new Response();
         response = userService.getAllUser();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-by-id/{userId}")
    public ResponseEntity<Response> getUserById(@PathVariable("userId") String userId){
    Response response = new Response();
         response = userService.getUserById(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    //get-logged-in-profile-info
    @GetMapping("/get-logged-in-profile-info")
    public ResponseEntity<Response> getLoggedInUserProfile(){
    Response response = new Response();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        response = userService.getMyInfo(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @GetMapping("/update-user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateUserRole(@PathVariable("userId")Long userId){
        Response response = new Response();
        response = userService.updateUser(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @GetMapping("/get-user-bookings-history/{userId}")
    public ResponseEntity<Response> getUserBookingHistory(@PathVariable("userId") String userId){
    Response response = new Response();
         response = userService.getUserBookingHistory(userId);
         return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<Response> deleteUser(@PathVariable("userId") String userId){
    Response response = new Response();
        response = userService.deleteUser(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
