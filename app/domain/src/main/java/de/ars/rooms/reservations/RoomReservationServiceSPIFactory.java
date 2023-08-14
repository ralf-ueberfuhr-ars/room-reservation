package de.ars.rooms.reservations;

import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class RoomReservationServiceSPIFactory {

    RoomReservationSink findSink() {
        final var sinks = ServiceLoader.load(RoomReservationSink.class)
                .stream().toList();
        if (sinks.isEmpty()) {
            throw new IllegalStateException("No room reservation sink implementation could be found.");
        }
        if (sinks.size() > 1) {
            final var sinksListString = sinks
                    .stream()
                    .map(sink -> sink.type().getName())
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("Multiple room reservation sink implementations could be found:  "
                    + sinksListString);

        }
        return sinks
                .iterator()
                .next()
                .get();
    }

    public RoomReservationService createService() {
        final var sink = findSink();
        return new RoomReservationService(sink);
    }

}
