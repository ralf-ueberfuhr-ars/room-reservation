package de.ars.rooms.reservations;

import java.time.LocalDate;
import java.util.UUID;

public record RoomReservation(
        UUID uuid,
        String room,
        LocalDate date
) {

}
