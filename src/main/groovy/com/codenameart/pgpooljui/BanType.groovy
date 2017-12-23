package com.codenameart.pgpooljui

/**
 * Created by Artem on 08.11.2017.
 */
enum BanType implements Type {
    SHADOW("Shadow ban", "shadowbanned = 1 AND banned != 1"),
    TEMP("Temp ban", "banned = 1"),
    ALL("Both", "shadowbanned = 1 OR banned = 1"),
    NONE("No ban", "shadowbanned != 1 AND banned != 1")

    BanType(String label, String predicate) {
        setup(label, predicate)
    }


    static BanType fromLabel(String label) {
        values().find {Type it -> it.label.compareToIgnoreCase(label)}
        return null
    }

}