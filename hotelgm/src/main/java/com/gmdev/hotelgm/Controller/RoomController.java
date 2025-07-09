package com.gmdev.hotelgm.Controller;

import com.gmdev.hotelgm.dto.Response;
import com.gmdev.hotelgm.dto.RoomDTO;
import com.gmdev.hotelgm.entity.Room;
import com.gmdev.hotelgm.service.interfac.IBookingService;
import com.gmdev.hotelgm.service.interfac.IRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    @Autowired
    private IRoomService roomService;
    @Autowired
    private IBookingService bookingService;
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> addNewRoom(@RequestParam(value = "photo",required=false)MultipartFile photo,
                                               @RequestParam(value="roomType",required=false)String roomType,
                                               @RequestParam(value="roomPrice",required = false)BigDecimal roomPrice,
                                               @RequestParam(value="roomDescription",required = false)String roomDescription
                                               ){
    Response response = new Response();
        if(photo== null || photo.isEmpty() || roomType == null || roomType.isBlank() || roomPrice == null){
            response.setStatusCode(400);
            response.setMessage("Please provide Values for all fields");
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }
        response = roomService.addNewRoom(photo,roomType,roomPrice,roomDescription);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllRooms(){
    Response response = new Response();
        response = roomService.getAllRooms();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/types")
    public List<String> getRoomTypes(){
    Response response = new Response();
         return roomService.getAllRoomTypes();

    }

    @GetMapping("/room-by-id/{roomId}")
    public ResponseEntity<Response> getRoomById(@PathVariable("roomId")Long roomId){
    Response response = new Response();
        response = roomService.getRoomById(roomId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all-available-rooms")
    public ResponseEntity<Response> getAllAvailableRooms(){
    Response response = new Response();
        response = roomService.getAllAvailableRooms();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all-available-rooms-by-date-and-types")
    public ResponseEntity<Response> getAllAvailableRoomsByDateAndType(
            @RequestParam(value = "checkInDate", required = true)@DateTimeFormat(iso= DateTimeFormat.ISO.DATE)LocalDate checkInDate,
            @RequestParam(value =  "checkOutDate",required = true)@DateTimeFormat(iso= DateTimeFormat.ISO.DATE)LocalDate checkOutDate,
            @RequestParam(value =  "roomType",required = false)String roomType
            ){
    Response response = new Response();
        if(checkInDate == null || roomType == null || roomType.isBlank() || checkOutDate == null){
            response.setStatusCode(400);
            response.setMessage("Please Provide Required details(CheckInDate and CheckOutDate)");
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }
        response = roomService.getAvailableRoomsByDateAndType(checkInDate,checkOutDate,roomType);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete-room/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteRoom(@PathVariable("roomId") Long roomId){
    Response response = new Response();
        response = roomService.deleteRoom(roomId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update-room/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateRoom(
            @PathVariable("roomId") Long roomId,
            @RequestParam(value = "roomType") String roomType,
            @RequestParam(value = "roomPrice")BigDecimal roomPrice,
            @RequestParam(value = "photo")MultipartFile photo,
            @RequestParam(value = "description")String description
            ){
        Response response = new Response();
        response = roomService.updateRoom(roomId,roomType,roomPrice,photo,description);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }






}
