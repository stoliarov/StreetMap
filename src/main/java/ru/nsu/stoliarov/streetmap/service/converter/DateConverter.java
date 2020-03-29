package ru.nsu.stoliarov.streetmap.service.converter;

import org.springframework.stereotype.Service;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.ZonedDateTime;

@Service
public class DateConverter {
	
	ZonedDateTime convert(XMLGregorianCalendar fromDateTime) {
		
		if (fromDateTime == null) {
			return null;
		}
		
		return ZonedDateTime.of(
				fromDateTime.getYear(),
				fromDateTime.getMonth(),
				fromDateTime.getDay(),
				fromDateTime.getHour(),
				fromDateTime.getMinute(),
				fromDateTime.getSecond(),
				0,
				fromDateTime.getTimeZone(0).toZoneId()
		);
	}
}
