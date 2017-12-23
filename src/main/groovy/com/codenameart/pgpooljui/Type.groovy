package com.codenameart.pgpooljui

/**
 * Created by Artem on 08.11.2017.
 */
trait Type {
    abstract String label
    abstract String predicate

    void setup(String label, String predicate) {
        this.label = label
        this.predicate = predicate
    }

    @Override
    String toString() {
        return label
    }

    String getLabel() {
        return label
    }

    String getPredicate() {
        return predicate
    }

    void setLabel(String label) {
        this.label = label
    }


    void setPredicate(String predicate) {
        this.predicate = predicate
    }

}
