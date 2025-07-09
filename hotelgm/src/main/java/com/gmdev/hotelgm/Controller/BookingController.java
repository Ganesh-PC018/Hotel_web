package com.gmdev.hotelgm.Controller;

import com.gmdev.hotelgm.dto.Response;
import com.gmdev.hotelgm.entity.Booking;
import com.gmdev.hotelgm.service.interfac.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    private IBookingService bookingService;

    @PostMapping("/book-room/{roomId}/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Response> saveBooking(@PathVariable("roomId") Long roomId, @PathVariable("userId") Long userId, @RequestBody Booking bookingRequest){
        Response response = new Response();
        response = bookingService.saveBooking(roomId,userId,bookingRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-by-confirmation-code/{confirmationCode}")
    public ResponseEntity<Response> findByConfirmationCode(@PathVariable("confirmationCode")String confirmationCode){
        Response response = new Response();
        response = bookingService.findBookingByConfirmationCode(confirmationCode);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all-bookings")
    public ResponseEntity<Response> getAllBookings(){
        Response response = new Response();
        response = bookingService.getAllBookings();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/cancel-booking/{bookingId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Response> cancelBooking(@PathVariable("bookingId")Long bookingId){
        Response response = new Response();
        response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}

