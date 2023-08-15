package de.ars.rooms.reservations.dynamodb;

import de.ars.rooms.reservations.RoomReservation;
import de.ars.rooms.reservations.RoomReservationSink;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class DynamoDBRoomReservationSink implements RoomReservationSink {

    private static final String TABLE = "reservations";
    private static final TableSchema<RoomReservationEntity> schema = TableSchema.fromBean(RoomReservationEntity.class);

    private static final Function<DynamoDbEnhancedClient, DynamoDbTable<RoomReservationEntity>> table
            = db -> db.table(TABLE, schema);

    private final DynamoDbEnhancedClient db;
    private final RoomReservationEntityMapper mapper;

    @SuppressWarnings("unused") // Java ServiceLoader SPI
    public DynamoDBRoomReservationSink() {
        this(
                DynamoDbEnhancedClient.create(),
                new RoomReservationEntityMapper()
        );
    }

    @Override
    public RoomReservation insertReservation(String room, LocalDate date) {
        // create uuid in DynamoDB is not possible!
        final var result = new RoomReservation(
                UUID.randomUUID(),
                room,
                date
        );
        table.apply(db).putItem(mapper.map(result));
        return result;
    }

    @Override
    public Optional<RoomReservation> findReservation(UUID uuid) {
        final var entity = table.apply(db)
                .getItem(
                        Key.builder()
                                .partitionValue(uuid.toString())
                                .build()
                );
        return Optional.ofNullable(entity)
                .map(mapper::map);
    }

    @Override
    public Optional<RoomReservation> findReservation(String room, LocalDate date) {
        // TODO create a global secondary index (GSI) to directly query on
        return table.apply(db)
                .scan(
                        ScanEnhancedRequest.builder()
                                .filterExpression(Expression.builder()
                                        .expression("room = :room and date = :date")
                                        .expressionValues(Map.of(
                                                ":room", AttributeValue.fromS(room),
                                                ":date", AttributeValue.fromS(date.format(DateTimeFormatter.ISO_DATE))
                                        ))
                                        .build())
                                .build()
                )
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst()
                .map(mapper::map);
    }

    @Override
    public Stream<RoomReservation> findReservations(String room) {
        // TODO create a global secondary index (GSI) to directly query on
        return table.apply(db)
                .scan(
                        ScanEnhancedRequest.builder()
                                .filterExpression(Expression.builder()
                                        .expression("room = :room")
                                        .expressionValues(Map.of(
                                                ":room", AttributeValue.fromS(room)
                                        ))
                                        .build())
                                .build()
                )
                .stream()
                .flatMap(page -> page.items().stream())
                .map(mapper::map);
    }
}
