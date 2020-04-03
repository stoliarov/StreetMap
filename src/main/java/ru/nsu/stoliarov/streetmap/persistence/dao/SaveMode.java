package ru.nsu.stoliarov.streetmap.persistence.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SaveMode {

    STATEMENT("statement"),

    PREPARED_STATEMENT("prepared_statement"),

    BATCH("batch");

    private String name;
}
