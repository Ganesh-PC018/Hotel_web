package com.gmdev.hotelgm.service.impl;

import com.gmdev.hotelgm.dto.BookingDTO;
import com.gmdev.hotelgm.dto.Response;
import com.gmdev.hotelgm.entity.Booking;
import com.gmdev.hotelgm.entity.Room;
import com.gmdev.hotelgm.entity.User;
import com.gmdev.hotelgm.exception.OurException;
import com.gmdev.hotelgm.repo.BookingRepository;
import com.gmdev.hotelgm.repo.RoomRepository;
import com.gmdev.hotelgm.repo.UserRepository;
import com.gmdev.hotelgm.service.interfac.IBookingService;
import com.gmdev.hotelgm.service.interfac.IRoomService;
import com.gmdev.hotelgm.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService implements IBookingService {
    @Autowired
     private BookingRepository bookingRepository;
    @Autowired
     private IRoomService roomService;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        Response response = new Response();
        try{
        if(bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
            throw new IllegalArgumentException("Check in date must come after check out date.");
        }
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
        User user = userRepository.findById(userId).orElseThrow(()-> new OurException("User not found"));
        List<Booking>  existingBooking = bookingRepository.findAll();
        if(!roomIsAvailable(bookingRequest,existingBooking)){
            throw new OurException("Room not Available for selected date range");
        }
        bookingRequest.setRoom(room);
        bookingRequest.setUser(user);
        String bookingConfirmationCode = Utils.generateRandomConfirmationCode(10);
        bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);
        bookingRepository.save(bookingRequest);
        response.setStatusCode(200);
        response.setMessage("Successful");
        response.setBookingConfirmationCode(bookingConfirmationCode);
        }catch (OurException e){
            response.setStatusCode(404);
            response.setMessage("Failed to Book the room");
        }catch (Exception e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }
    public static boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {
        return existingBookings.stream().noneMatch(existingBooking ->
                bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()) &&
                        bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckInDate())
        );
    }
    @Override
    public Response findBookingByConfirmationCode(String confirmationCode) {
        Response response = new Response();
        try {
            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(() -> new OurException("Booking not found with confirmation code: " + confirmationCode));
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTOPlusBookedRoom(booking, true);
            System.out.println("Booking Entity: " + booking);
            System.out.println("BookingDTO Entity: " + bookingDTO);
            response.setBooking(bookingDTO);
            response.setUser(bookingDTO.getUser());
            response.setRoom(bookingDTO.getRoom());
//            response.setBooking(bookingDTO);
            response.setStatusCode(200);
            response.setMessage("Successful");
            System.out.println("Setting booking in response...");
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage("Error: " + e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Internal Server Error: " + e.getMessage());
        }
        return response;
    }


    @Override
    public Response getAllBookings() {
        Response response = new Response();
        try{
            List<Booking> bookingList = bookingRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
            List<BookingDTO> bookingDTOList = Utils.mapBookingListEntityToBookingListDTO(bookingList);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setBookingList(bookingDTOList);
        }catch (OurException e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        }catch (Exception e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response cancelBooking(Long bookingId) {
        Response response = new Response();
        Booking booking  = bookingRepository.findById(bookingId).orElseThrow(()-> new OurException("Could not find the Booking Id"));
        bookingRepository.delete(booking);
        response.setStatusCode(200);
        response.setMessage("Successful");
        return response;
    }
}
