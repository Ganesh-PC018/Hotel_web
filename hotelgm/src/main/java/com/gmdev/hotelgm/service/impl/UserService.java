package com.gmdev.hotelgm.service.impl;

import com.gmdev.hotelgm.dto.LoginRequest;
import com.gmdev.hotelgm.dto.Response;
import com.gmdev.hotelgm.dto.UserDTO;
import com.gmdev.hotelgm.entity.Booking;
import com.gmdev.hotelgm.entity.User;
import com.gmdev.hotelgm.exception.OurException;
import com.gmdev.hotelgm.repo.BookingRepository;
import com.gmdev.hotelgm.repo.UserRepository;
import com.gmdev.hotelgm.service.interfac.IUserService;
import com.gmdev.hotelgm.utils.JWTUtils;
import com.gmdev.hotelgm.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Response register(User user) {
    Response response = new Response();
        try{
           if(user.getRole() == null || user.getRole().isBlank()){
               user.setRole("USER");
           }
           if(userRepository.existsByEmail(user.getEmail())){
               throw new OurException(user.getEmail() +"Already Exists");
           }
           user.setPassword(passwordEncoder.encode(user.getPassword()));
           User savedUser = userRepository.save(user);
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(savedUser);
            response.setStatusCode(200);
            response.setUser(userDTO);
        }catch (OurException e){
            response.setStatusCode(400);
            response.setMessage("error occurred During User Registration "+e.getMessage());

        }
        return response;
    }

    @Override
    public Response login(LoginRequest loginRequest) {
    Response response = new Response();
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
            var user =userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()->new OurException(("User Not found")));
            var jwt =jwtUtils.generateToken(user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setExpirationTime("7 Days");
            response.setMessage("Successful");
        }catch (OurException e)
        {
            response.setStatusCode(404);
            response.setMessage("No User Found");
        } catch(Exception e){
            response.setStatusCode(400);
            response.setMessage("error occurred During User Registration "+e.getMessage());

        }
        return response;
    }

    @Override
    public Response updateUser(Long userId) {
        Response response = new Response();
        try{
            User user = userRepository.findById(userId).orElseThrow(()-> new OurException("Could not Found Room"));
            if(userId == 2){
                response.setStatusCode(400);
                response.setMessage("Can't Change Super Admin Permission");
                return response;
            }
            if(user.getRole().equals("ADMIN")){
                user.setRole("USER");
            }else{
                user.setRole("ADMIN");
            }
            User updatedUser = userRepository.save(user);
            System.out.println(updatedUser);
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(updatedUser);
            response.setStatusCode(200);
            response.setMessage("Successfully");
            response.setUser(userDTO);
        }catch(OurException e){
            response.setStatusCode(400);
            response.setMessage("No User found");
        }
        return response;
    }

    @Override
    public Response getAllUser() {
    Response response = new Response();
        try{
            List<User> userList = userRepository.findAll();
            List<UserDTO> userDTOList = Utils.mapUserListEntityToUserListDTO(userList);
            response.setStatusCode(200);
            response.setUserList(userDTOList);
            response.setMessage("Successful");
        }catch(OurException e){
            response.setStatusCode(400);
            response.setMessage("error occurred During Fetching All Users. "+e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserBookingHistory(String userId) {
    Response response = new Response();
        try{
            User user  = userRepository.findById(Long.valueOf(userId)).orElseThrow(()-> new OurException("User Not Found."));
            UserDTO userDTO =Utils.mapUserEntityToUserDTOPlusUserBookingsAndRoom(user);
            response.setStatusCode(200);
            response.setUser(userDTO);
            response.setMessage("Successful");
        }catch(OurException e){
            response.setStatusCode(404);
            response.setMessage("error occurred During User Getting History "+e.getMessage());
        }catch (Exception e){
            response.setStatusCode(400);
            response.setMessage("No User Found"+e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteUser(String userId) {
    Response response = new Response();
        try{
            userRepository.findById(Long.valueOf(userId)).orElseThrow(()-> new OurException("Error While Deleting User."));
            userRepository.deleteById(Long.valueOf(userId));
            response.setStatusCode(200);
            response.setMessage("Successful");
        }catch(OurException e){
            response.setStatusCode(400);
            response.setMessage("error occurred While deleting. "+e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserById(String userId) {
    Response response = new Response();
        try{
            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(()-> new OurException("Error While Deleting User."));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setUser(userDTO);
        }catch(OurException e){
            response.setStatusCode(400);
            response.setMessage("error occurred While deleting. "+e.getMessage());
        }
        return response;
    }

    @Override
    public Response getMyInfo(String email) {
    Response response = new Response();
        try{
            User user = userRepository.findByEmail(email).orElseThrow(()-> new OurException("Error While Deleting User."));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setUser(userDTO);
        }catch(OurException e){
            response.setStatusCode(400);
            response.setMessage("error occurred While deleting. "+e.getMessage());
        }
        return response;
    }
}
