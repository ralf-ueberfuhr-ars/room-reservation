package de.ars.rooms.reservations.lambda;

import de.ars.rooms.reservations.RoomReservationService;
import de.ars.rooms.reservations.RoomReservationServiceSPIFactory;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RoomReservationServiceHolder {

    public final RoomReservationService SERVICE = new RoomReservationServiceSPIFactory()
            .createService();

}
