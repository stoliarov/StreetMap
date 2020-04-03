package ru.nsu.stoliarov.streetmap.retention.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nsu.stoliarov.streetmap.persistence.dao.SaveMode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetentionOptions {

    private SaveMode saveMode;

    private Integer batchSize;
}
