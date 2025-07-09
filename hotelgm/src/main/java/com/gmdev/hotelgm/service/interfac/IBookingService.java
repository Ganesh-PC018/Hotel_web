package com.gmdev.hotelgm.service.interfac;

import com.gmdev.hotelgm.dto.Response;
import com.gmdev.hotelgm.entity.Booking;

public interface IBookingService {
    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);
    Response findBookingByConfirmationCode(String confirmationCode);
    Response getAllBookings();
    Response cancelBooking(Long bookingId);
}
