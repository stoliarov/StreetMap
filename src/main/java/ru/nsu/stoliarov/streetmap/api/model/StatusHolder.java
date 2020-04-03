package ru.nsu.stoliarov.streetmap.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusHolder {

    private Status status;

    private String message = "";

    public StatusHolder(Status status) {
        this.status = status;
    }

    public StatusHolder(StatusHolder statusHolder) {

        if (statusHolder == null) {
            return;
        }

        this.status = statusHolder.getStatus();
        this.message = statusHolder.getMessage();
    }

    public static StatusHolder buildSuccessStatus() {
        return new StatusHolder(Status.SUCCESS);
    }

    public static StatusHolder buildFaultStatus(String message) {
        return new StatusHolder(Status.FAILED, message);
    }
}
