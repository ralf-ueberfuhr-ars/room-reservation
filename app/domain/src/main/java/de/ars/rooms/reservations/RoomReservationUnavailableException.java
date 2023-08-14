package de.ars.rooms.reservations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
public class RoomReservationUnavailableException extends Exception {

    private final String room;
    private final LocalDate date;

}
