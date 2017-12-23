package com.codenameart.pgpooljui

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

/**
 * Created by Artem on 08.11.2017.
 */
@Component
public class ReleaseService {
    @Autowired
    private JdbcTemplate jdbcTemplate

    public int release(ReleaseQuery releaseQuery) {
        String filedName
        if (releaseQuery.getBanType() == BanType.SHADOW) {
            filedName = "shadowbanned"
        } else {
            filedName = "banned"
        }

        String whereClause = "datediff(now(), last_modified) = ${releaseQuery.getTimeout()}" +
                " AND ${releaseQuery.getWorkerType().getPredicate()}"


        return jdbcTemplate.update("UPDATE account SET $filedName = 0 WHERE $whereClause")
    }


}
