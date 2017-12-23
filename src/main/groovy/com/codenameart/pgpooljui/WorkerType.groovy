package com.codenameart.pgpooljui

/**
 * Created by Artem on 08.11.2017.
 */
enum WorkerType implements Type {
    WORKER("Worker", "level < 30"),
    HLVL("High level", "level >= 30"),
    ALL("All", "level >= 0")

    WorkerType(String label, String predicate) {
        setup(label, predicate)
    }


    static WorkerType fromLabel(String label) {
        values().find { Type it -> it.label.compareToIgnoreCase(label) }
        return null
    }
}
