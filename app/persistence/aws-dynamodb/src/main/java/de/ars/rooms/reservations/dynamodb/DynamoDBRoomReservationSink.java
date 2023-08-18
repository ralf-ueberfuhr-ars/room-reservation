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
import java.util.stream.Stream;

@RequiredArgsConstructor
public class DynamoDBRoomReservationSink implements RoomReservationSink {

    private static final String ENV_ROOM_RESERVATIONS_TABLE = "ROOM_RESERVATIONS_TABLE";
    private static final String DEFAULT_ROOM_RESERVATIONS_TABLE = "room-reservations";
    private static final TableSchema<RoomReservationEntity> schema = TableSchema.fromBean(RoomReservationEntity.class);

    private final DynamoDbEnhancedClient db;
    private final String tableName;
    private final RoomReservationEntityMapper mapper;

    @SuppressWarnings("unused") // Java ServiceLoader SPI
    public DynamoDBRoomReservationSink() {
        this(DynamoDbEnhancedClient.create(), Optional.ofNullable(System.getenv(ENV_ROOM_RESERVATIONS_TABLE)).orElse(DEFAULT_ROOM_RESERVATIONS_TABLE), new RoomReservationEntityMapper());
    }

    protected DynamoDbTable<RoomReservationEntity> getTable() {
        return this.db.table(this.tableName, schema);
    }

    @Override
    public RoomReservation insertReservation(String room, LocalDate date) {
        // create uuid in DynamoDB is not possible!
        final var result = new RoomReservation(UUID.randomUUID(), room, date);
        getTable().putItem(mapper.map(result));
        return result;
    }

    @Override
    public Optional<RoomReservation> findReservation(UUID uuid) {
        final var entity = getTable().getItem(Key.builder().partitionValue(uuid.toString()).build());
        return Optional.ofNullable(entity).map(mapper::map);
    }

    @Override
    public Optional<RoomReservation> findReservation(String room, LocalDate date) {
        // TODO create a global secondary index (GSI) to directly query on
        return getTable().scan(ScanEnhancedRequest.builder().filterExpression(Expression.builder()
                // date is a reserved keyword
                .expression("room = :room and #date = :date").expressionNames(Map.of("#date", "date")).expressionValues(Map.of(":room", AttributeValue.fromS(room), ":date", AttributeValue.fromS(date.format(DateTimeFormatter.ISO_DATE)))).build()).build()).stream().flatMap(page -> page.items().stream()).findFirst().map(mapper::map);
    }

    @Override
    public Stream<RoomReservation> findReservations(String room) {
        // TODO create a global secondary index (GSI) to directly query on
        return getTable().scan(ScanEnhancedRequest.builder().filterExpression(Expression.builder().expression("room = :room").expressionValues(Map.of(":room", AttributeValue.fromS(room))).build()).build()).stream().flatMap(page -> page.items().stream()).map(mapper::map);
    }

}
