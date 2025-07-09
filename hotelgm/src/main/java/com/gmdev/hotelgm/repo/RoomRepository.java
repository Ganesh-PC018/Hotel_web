package com.gmdev.hotelgm.repo;

import com.gmdev.hotelgm.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.*;

public interface RoomRepository extends JpaRepository<Room,Long> {
    @Query("SELECT DISTINCT r.roomType from Room r")
    ArrayList<String> findDistinctRoomTypes();

    @Query("Select r from Room r Where r.roomType Like %:roomType% AND r.id NOT IN (Select bk.room.id from Booking bk where (bk.checkInDate <= :checkOutDate) AND (bk.checkOutDate >= :checkInDate))")
    ArrayList<Room> findAvailableRoomsByDatesAndType(LocalDate checkInDate,LocalDate checkOutDate,String roomType);
    @Query("Select r from Room r WHERE r.id NOT IN (SELECT b.room.id FROM Booking b)")
    List<Room> getAllAvailableRooms();
}
