package ru.nsu.stoliarov.streetmap.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.nsu.stoliarov.streetmap.model.Way;

@Data
@EqualsAndHashCode(callSuper = true)
public class WayUpdateResponse extends StatusHolder {

    private Way way;

    public WayUpdateResponse(Way way) {
        super(StatusHolder.buildSuccessStatus());
        this.way = way;
    }

    public WayUpdateResponse(StatusHolder statusHolder) {
        super(statusHolder);
    }
}
