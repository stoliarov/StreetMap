package ru.nsu.stoliarov.streetmap.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nsu.stoliarov.streetmap.persistence.dao.SaveMode;

import java.util.List;

@Data
@NoArgsConstructor
public class SpeedTestResponse {

    private List<ResultItem> result;

    private int batchSize;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultItem {

        private SaveMode saveMode;

        private long recordCount;

        private double recordPerSecond;
    }
}
