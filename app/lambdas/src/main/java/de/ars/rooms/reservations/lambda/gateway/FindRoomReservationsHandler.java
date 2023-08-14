package de.ars.rooms.reservations.lambda.gateway;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ars.rooms.reservations.RoomReservation;
import de.ars.rooms.reservations.RoomReservationService;
import de.ars.rooms.reservations.lambda.RoomReservationServiceHolder;
import de.ars.rooms.reservations.lambda.jackson.ObjectMapperHolder;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Finds a room's reservations.
 */
@Log4j2
public class FindRoomReservationsHandler
        implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    public static final String ROOM_PATH_PARAMETER = "room";
    public static final String DATE_REQ_PARAMETER = "date";

    private static final RoomReservationService service = RoomReservationServiceHolder.SERVICE;
    private static final ObjectMapper mapper = ObjectMapperHolder.MAPPER;

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
        // TODO isn't there any streaming api?
        final var room = input.getPathParameters().get(ROOM_PATH_PARAMETER);
        if (null == room) {
            log.debug("BAD REQUEST: No room was specified.");
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(400)
                    .withBody("No room was specified.")
                    .build();
        }
        final var reservations = Optional
                .ofNullable(input.getQueryStringParameters().get(DATE_REQ_PARAMETER))
                .map(LocalDate::parse)
                .map(date -> findReservationByDate(room, date))
                .orElse(findReservations(room))
                .collect(Collectors.toList());
        try {
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody(mapper.writeValueAsString(reservations))
                    .withHeaders(Map.of("Content-Type", "application/json"))
                    .build();
        } catch (JsonProcessingException e) {
            log.error("Error on writing response.", e);
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(500)
                    .build();
        }

    }

    protected Stream<RoomReservation> findReservationByDate(String room, LocalDate date) {
        return service
                .findReservationByRoomAndDate(room, date)
                .stream();
    }

    protected Stream<RoomReservation> findReservations(String room) {
        return service
                .findReservationsByRoom(room);
    }

}
