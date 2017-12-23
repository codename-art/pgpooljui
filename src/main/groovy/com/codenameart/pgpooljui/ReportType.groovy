package com.codenameart.pgpooljui

/**
 * Created by Artem on 10.11.2017.
 */
public enum ChartType implements Type {
    DATE("Date", ""),
    TIMEOUT("Timeout", "")

    ChartType(String label, String predicate) {
        setup(label, predicate)
    }
}