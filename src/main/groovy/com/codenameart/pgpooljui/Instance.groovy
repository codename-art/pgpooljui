package com.codenameart.pgpooljui
/**
 * Created by Artem on 27.10.2017.
 */
public class Instance {
    String systemId
    String lastUpdated
    int activeWorkers
    int activeHlvl

    public Instance(String systemId, String lastUpdated, int activeWorkers, int activeHlvl) {
        this.systemId = systemId
        this.lastUpdated = lastUpdated
        this.activeWorkers = activeWorkers
        this.activeHlvl = activeHlvl
    }
}
