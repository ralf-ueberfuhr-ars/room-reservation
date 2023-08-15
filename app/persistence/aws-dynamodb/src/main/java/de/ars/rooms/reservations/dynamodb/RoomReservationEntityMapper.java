package de.ars.rooms.reservations.dynamodb;

import de.ars.rooms.reservations.RoomReservation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class RoomReservationEntityMapper {

    public RoomReservationEntity map(RoomReservation source) {
        if (null == source) {
            return null;
        }
        final var target = new RoomReservationEntity();
        if (source.uuid() != null) {
            target.setUuid(source.uuid().toString());
        }
        target.setRoom(source.room());
        if (source.date() != null) {
            target.setDate(source.date().format(DateTimeFormatter.ISO_DATE));
        }
        return target;
    }

    public RoomReservation map(RoomReservationEntity source) {
        if (null == source) {
            return null;
        }
        return new RoomReservation(
                source.getUuid() != null ? UUID.fromString(source.getUuid()) : null,
                source.getRoom(),
                source.getDate() != null ? LocalDate.parse(source.getDate(), DateTimeFormatter.ISO_DATE) : null
        );
    }

}
