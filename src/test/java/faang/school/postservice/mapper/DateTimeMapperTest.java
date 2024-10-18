package faang.school.postservice.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;


class DateTimeMapperTest {

    private final DateTimeMapper dateTimeMapper = new DateTimeMapper() {};

    @Test
    void localDateTimeToTimestamp_shouldConvertLocalDateTimeToProtobufTimestamp() {
        LocalDateTime localDateTime = LocalDateTime.of(2024, 10, 14, 12, 30, 45, 123456789);
        long expectedSeconds = localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
        int expectedNanos = localDateTime.getNano();

        Timestamp actualTimestamp = dateTimeMapper.localDateTimeToTimestamp(localDateTime);

        assertNotNull(actualTimestamp);
        assertEquals(expectedSeconds, actualTimestamp.getSeconds());
        assertEquals(expectedNanos, actualTimestamp.getNanos());
    }

    @Test
    void localDateTimeToTimestamp_shouldReturnNullWhenLocalDateTimeIsNull() {
        Timestamp timestamp = dateTimeMapper.localDateTimeToTimestamp(null);

        assertNull(timestamp);
    }

    @Test
    void localDateTimeToTimestamp_shouldHandleEdgeCases() {
        LocalDateTime localDateTime = LocalDateTime.of(1941, 1, 1, 0, 0);

        long expectedSeconds = localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
        int expectedNanos = localDateTime.getNano();

        Timestamp actualTimestamp = dateTimeMapper.localDateTimeToTimestamp(localDateTime);

        assertNotNull(actualTimestamp);
        assertEquals(expectedSeconds, actualTimestamp.getSeconds());
        assertEquals(expectedNanos, actualTimestamp.getNanos());
    }
}
