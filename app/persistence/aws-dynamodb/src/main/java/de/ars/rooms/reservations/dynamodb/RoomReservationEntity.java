package de.ars.rooms.reservations.dynamodb;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Getter
@Setter
@DynamoDbBean
public class RoomReservationEntity {

    // TODO type converters for UUID and LocalDate?

    @Getter(onMethod_ = @DynamoDbPartitionKey)
    private String uuid;
    private String room;
    private String date;

}
