package ru.nsu.stoliarov.streetmap.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {

    SUCCESS("success"),

    FAILED("failed");

    private String name;
}
