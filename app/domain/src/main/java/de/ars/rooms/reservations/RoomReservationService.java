package de.ars.rooms.reservations;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Log4j2
public class RoomReservationService {

    private final RoomReservationSink sink;

    /**
     * Does a reservation for the given room at the given date.
     *
     * @param room the room's number
     * @param date the date
     * @return the room reservation
     * @throws RoomReservationUnavailableException if the room is still reserved at the given date
     * @throws IllegalArgumentException     if the given room's number is null, or the given date is null or in the past
     */
    public RoomReservation createReservation(String room, LocalDate date)
            throws RoomReservationUnavailableException {

        log.debug("Make a reservation for room {} at {}...", room, date);
        // validation -> we could include Jakarta Bean Validation here
        if (room == null || date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException();
        }

        log.debug("Check room {} for not already having a reservation at {}", room, date);
        if (sink.findReservation(room, date).isPresent()) {
            throw new RoomReservationUnavailableException(room, date);
        }

        log.debug("Saving reservation for room {} at {} ...", room, date);
        final var result = sink.insertReservation(room, date);
        log.debug("Finished room reservation successfully.");
        return result;

    }

    /**
     * Searches for a reservation for a room at a given date.
     *
     * @param room the room's number
     * @param date the date
     * @return the room reservation or an empty optional
     * @throws IllegalArgumentException if the given room's number is null, or the given date is null
     */
    public Optional<RoomReservation> findReservationByRoomAndDate(String room, LocalDate date) {
        // validation -> we could include Jakarta Bean Validation here
        if (room == null || date == null) {
            throw new IllegalArgumentException();
        }
        return sink.findReservation(room, date);
    }

    /**
     * Searches for all reservations of a given room.
     * @param room the room's number
     * @return the stream of reservations
     */
    public Stream<RoomReservation> findReservationsByRoom(String room) {
        // validation -> we could include Jakarta Bean Validation here
        if (room == null) {
            throw new IllegalArgumentException();
        }
        return sink.findReservations(room);
    }

}
