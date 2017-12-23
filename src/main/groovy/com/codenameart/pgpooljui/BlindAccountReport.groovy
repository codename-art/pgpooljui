package com.codenameart.pgpooljui
/**
 * Created by Artem on 27.10.2017.
 */
class BlindAccountReport {
    int count
    int daysSinceBlinded
    Date blindDate

    BlindAccountReport(int count, int daysSinceBlinded) {
        this.count = count
        this.daysSinceBlinded = daysSinceBlinded

    }

    BlindAccountReport(int count, Date blindDate) {
        this.count = count
        this.blindDate = blindDate
    }


    public String toStringWithTimeout() {
        return "[" + "'" + daysSinceBlinded + "', " + count + ']';
    }

    public String toStringWithDate() {
        return "[" + "'" + blindDate.format("yyyy-MM-dd") + "', " + count + ']';
    }
}
