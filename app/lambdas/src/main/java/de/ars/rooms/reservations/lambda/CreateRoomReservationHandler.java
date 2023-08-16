package de.ars.rooms.reservations.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import de.ars.rooms.reservations.RoomReservationService;
import de.ars.rooms.reservations.RoomReservationUnavailableException;
import de.ars.rooms.reservations.lambda.jackson.AbstractJacksonRequestHandler;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;

@Log4j2
@SuppressWarnings("unused") // invoked by AWS Lambda
public class CreateRoomReservationHandler
        extends AbstractJacksonRequestHandler<RoomReservationRequest, RoomReservationResult> {

    // initialization
    private static final RoomReservationService service = RoomReservationServiceHolder.SERVICE;

    @Override
    protected Class<? extends RoomReservationRequest> getInputClass() {
        return RoomReservationRequest.class;
    }

    @Override
    protected RoomReservationResult handleRequestWithObjects(RoomReservationRequest input, Context context) {
        if(input.getRoomNumber() == null || input.getDate() == null || input.getDate().isBefore(LocalDate.now())) {
            log.debug(
                    "The reservation request is invalid (room '{}', date {}).",
                    input.getRoomNumber(),
                    input.getDate()
            );
            return RoomReservationResult.invalid();
        }
        try {
            final var reservation = service.createReservation(input.getRoomNumber(), input.getDate());
            log.debug(
                    "Successfully reserved room '{}' on {}.",
                    input.getRoomNumber(),
                    input.getDate()
            );
            return RoomReservationResult.ok(reservation);
        } catch (RoomReservationUnavailableException e) {
            log.debug(
                    "Could not reserve room '{}' on {} because it is not available.",
                    input.getRoomNumber(),
                    input.getDate()
            );
            return RoomReservationResult.unavailable();
        }
    }
}
