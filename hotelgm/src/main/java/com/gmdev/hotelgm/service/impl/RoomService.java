package com.gmdev.hotelgm.service.impl;

import com.gmdev.hotelgm.dto.Response;
import com.gmdev.hotelgm.dto.RoomDTO;
import com.gmdev.hotelgm.entity.Booking;
import com.gmdev.hotelgm.entity.Room;
import com.gmdev.hotelgm.entity.User;
import com.gmdev.hotelgm.exception.OurException;
import com.gmdev.hotelgm.repo.BookingRepository;
import com.gmdev.hotelgm.repo.RoomRepository;
import com.gmdev.hotelgm.service.AwsS3Service;
import com.gmdev.hotelgm.service.interfac.IRoomService;
import com.gmdev.hotelgm.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService implements IRoomService {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private AwsS3Service awsS3Service;
    @Override
    public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
    Response response = new Response();
       try{
           String imageUrl = awsS3Service.saveImageToS3(photo);
           Room room = new Room();
//           room.setId(room.getId());
           room.setRoomPhotoUrl(imageUrl);
           room.setRoomType(roomType);
           room.setRoomPrice(roomPrice);
           room.setRoomDescription(description);
           Room savedRoom = roomRepository.save(room);
           RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(savedRoom);
           response.setStatusCode(200);
           response.setMessage("Successful");
           response.setRoom(roomDTO);
       }catch (OurException e){
           response.setStatusCode(400);
           response.setMessage("Could not add room "+e.getMessage());
       }catch (Exception e){
           response.setStatusCode(404);
           response.setMessage("Error While Adding Image to S3 "+e.getMessage());
       }
       return response;
    }

    @Override
    public List<String> getAllRoomTypes() {
    Response response = new Response();
            List<String> roomTypeList = roomRepository.findDistinctRoomTypes();
            return roomTypeList;
    }

    @Override
    public Response deleteRoom(Long roomId) {
    Response response = new Response();
        try{
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room not available."));
            roomRepository.delete(room);
            response.setStatusCode(200);
            response.setMessage("Successful");
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage("Error while deleting rooms. "+e.getMessage());
        }catch (Exception e){
            response.setStatusCode(400);
            response.setMessage("No Room Found."+e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateRoom(Long roomId, String roomType, BigDecimal roomPrice, MultipartFile photo,String description) {
    Response response = new Response();
        try{
          String imageUrl = null;
          if(photo != null && !photo.isEmpty()){
            imageUrl = awsS3Service.saveImageToS3(photo);
          }
            Room room  = roomRepository.findById(roomId).orElseThrow(()->new OurException("No room Available"));
            if(roomType != null)
                room.setRoomType(roomType);
            if(description != null)
                room.setRoomDescription(description);
            if(roomPrice != null)
                room.setRoomPrice(roomPrice);
            if(imageUrl != null)
                room.setRoomPhotoUrl(imageUrl);
            room.setId(roomId);
            Room updatedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(updatedRoom);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoom(roomDTO);
        }catch(OurException e){
            response.setStatusCode(404);
            response.setMessage("Error while updating room. "+e.getMessage());
        }catch (Exception e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response getRoomById(Long roomId) {
    Response response = new Response();
        try{
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("room Not Available"));
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTOPlusBookings(room);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoom(roomDTO);
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage("Error While Finding room"+e.getMessage());
        }catch (Exception e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAvailableRoomsByDateAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
    Response response = new Response();
        try{
            List<Room> roomList = roomRepository.findAvailableRoomsByDatesAndType(checkInDate,checkOutDate,roomType);
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoomList(roomDTOList);
        }catch (OurException e){
            response.setStatusCode(400);
            response.setMessage("Error While Finding room"+e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllAvailableRooms() {
    Response response = new Response();
        try{
        List<Room> roomList = roomRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
        response.setStatusCode(200);
        response.setMessage("Successful");
        response.setRoomList(roomDTOList);
        }catch(OurException e){
            response.setStatusCode(400);
            response.setMessage("error while fetching list of rooms "+e.getMessage());
        }
        return response;
    }

    @Override
    public  Response getAllRooms(){
    Response response = new Response();
        try{
            List<Room> roomList = roomRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoomList(roomDTOList);
        }catch(OurException e){
            response.setStatusCode(400);
            response.setMessage("error while fetching list of rooms "+e.getMessage());
        }

        return response;
    }
}
