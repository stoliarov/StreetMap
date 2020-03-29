package ru.nsu.stoliarov.streetmap.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpeedTestRequest {

    private Integer batchSize;
}
