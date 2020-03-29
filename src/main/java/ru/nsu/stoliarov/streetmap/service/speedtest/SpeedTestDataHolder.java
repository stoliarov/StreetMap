package ru.nsu.stoliarov.streetmap.service.speedtest;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class SpeedTestDataHolder {

    /**
     * Суммарное количество миллисекунд, потраченных при вызове методов save/flush для записи данных.
     */
    private Long milliseconds;

    private Long recordCount;

    public SpeedTestDataHolder() {
        this.reset();
    }

    public void reset() {
        milliseconds = 0L;
        recordCount = 0L;
    }
}
