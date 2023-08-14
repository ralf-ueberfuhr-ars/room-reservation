package de.ars.rooms.reservations;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * A sink is the interface to the persistence layer.
 * Persistence providers implement this interface using Java SPI to provide access to a database.
 */
public interface RoomReservationSink {

    /**
     * Stores the reservation into the database. This will assign a UUID to identify the reservation.
     * @param room the room
     * @param date the date of reservation
     * @return the room reservation incl. the UUID
     */
    RoomReservation insertReservation(String room, LocalDate date);

    /**
     * Finds a reservation by UUID.
     * @param uuid the UUID
     * @return the reservation or an empty {@link Optional}
     */
    Optional<RoomReservation> findReservation(UUID uuid);

    /**
     * Finds a reservation by room and date.
     * @param room the room
     * @param date the date of reservation
     * @return the reservation or an empty {@link Optional}
     */
    Optional<RoomReservation> findReservation(String room, LocalDate date);

    /**
     * Finds all reservations by room.
     * @param room the room
     * @return the reservations
     */
    Stream<RoomReservation> findReservations(String room);
}
