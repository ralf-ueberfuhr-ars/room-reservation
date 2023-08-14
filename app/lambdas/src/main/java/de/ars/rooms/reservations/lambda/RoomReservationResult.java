package de.ars.rooms.reservations.lambda;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.ars.rooms.reservations.RoomReservation;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder(access = AccessLevel.PRIVATE)
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomReservationResult {

    public enum RoomReservationStatus {

        SUCCESSFUL, INVALID, UNAVAILABLE

    }

    private RoomReservationStatus status;
    private RoomReservation reservation;

    public static RoomReservationResult ok(RoomReservation reservation) {
        return RoomReservationResult
                .builder()
                .status(RoomReservationStatus.SUCCESSFUL)
                .reservation(reservation)
                .build();
    }

    public static RoomReservationResult unavailable() {
        return RoomReservationResult
                .builder()
                .status(RoomReservationStatus.UNAVAILABLE)
                .build();
    }

    public static RoomReservationResult invalid() {
        return RoomReservationResult
                .builder()
                .status(RoomReservationStatus.INVALID)
                .build();
    }

}
