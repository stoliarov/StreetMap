package ru.nsu.stoliarov.streetmap.service.persistence.util;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
@NoArgsConstructor
public class DateTimeConverter {

    private static final DateTimeFormatter WITH_ZONE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssz");

    private static final DateTimeFormatter WITHOUT_ZONE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String convertToPostgresString(ZonedDateTime zonedDateTime) {

        if (zonedDateTime == null) {
            return null;
        }

        return zonedDateTime.format(WITH_ZONE_FORMATTER);
    }

    public Timestamp convertToTimestamp(ZonedDateTime zonedDateTime) {

        if (zonedDateTime == null) {
            return null;
        }

        return Timestamp.valueOf(zonedDateTime.format(WITHOUT_ZONE_FORMATTER));
    }
}
