package com.codenameart.pgpooljui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Created by Artem on 20.10.2017.
 * Java class instead of Groovy for method references, waiting Groovy 3
 */
@Component
class AccountService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Account> findAll() {
        return jdbcTemplate.query(
                "SELECT username, level, shadowbanned, warn, banned, captcha, last_modified FROM account",
                (rs, rowNum) -> new Account(
                        rs.getString("username"),
                        rs.getShort("level"),
                        rs.getBoolean("shadowbanned"),
                        rs.getBoolean("warn"),
                        rs.getBoolean("banned"),
                        rs.getBoolean("captcha"),
                        rs.getString("last_modified"))
        );
    }

    public int countTotal() {
        return sum(this::getTotal);
    }

    public int countTotalUsed() {
        return sumNotNull(this::getTotal);
    }

    public int countTotalReadyToUse() {
        return sum(this::getReadyToUse);
    }

    public int countTotalShadow() {
        return sum(this::getShadow);
    }

    public int countTotalBanned() {
        return sum(this::getBanned);
    }


    public int countWorkers() {
        return sum(this::getWorkers);
    }

    public int countWorkersUsed() {
        return sumNotNull(this::getWorkers);
    }

    public int countWorkersReadyToUse() {
        return getReadyToUse().getOrDefault("worker", 0);
    }

    public int countWorkersShadow() {
        return getShadow().getOrDefault("worker", 0);
    }

    public int countWorkersBanned() {
        return getBanned().getOrDefault("worker", 0);
    }


    public int countHlvl() {
        return sum(this::getHlvl);
    }


    public int countHlvlUsed() {
        return sumNotNull(this::getHlvl);
    }

    public int countHlvlReadyToUse() {
        return getReadyToUse().getOrDefault("hlvl", 0);
    }

    public int countHlvlShadow() {
        return getShadow().getOrDefault("hlvl", 0);
    }

    public int countHlvlBanned() {
        return getBanned().getOrDefault("hlvl", 0);
    }

    private int sum(Supplier<Map<String, Integer>> getMapFromDB) {
        return getMapFromDB.get()
                .values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    private int sumNotNull(Supplier<Map<String, Integer>> getMapFromDB) {
        return getMapFromDB.get()
                .entrySet()
                .stream()
                .filter(e -> !Objects.equals(e.getKey(), "FREE"))
                .mapToInt(Map.Entry::getValue)
                .sum();
    }


    public Map<String, Integer> getReadyToUse() {
        return getWithFlags("system_id IS NULL AND " + BanType.NONE.getPredicate());
    }

    public Map<String, Integer> getShadow() {
        return getWithFlags(BanType.SHADOW.getPredicate());
    }

    public Map<String, Integer> getBanned() {
        return getWithFlags(BanType.TEMP.getPredicate());
    }

    private Map<String, Integer> getTotal() {
        return jdbcTemplate.query(
                "SELECT IFNULL(system_id, 'FREE') AS system_id, count(*) AS c " +
                        "FROM account " +
                        "GROUP BY IFNULL(system_id, 'FREE')",
                (ResultSet rs) -> {
                    HashMap<String, Integer> results = new HashMap<>();
                    while (rs.next()) {
                        results.put(rs.getString("system_id"), rs.getInt("c"));
                    }
                    return results;
                }
        );
    }

    public Map<String, Integer> getWorkers() {
        return jdbcTemplate.query(
                "SELECT IFNULL(system_id, 'FREE') AS system_id, count(*) AS c " +
                        "FROM account " +
                        "WHERE level < 30 " +
                        "GROUP BY IFNULL(system_id, 'FREE')",
                (ResultSet rs) -> {
                    HashMap<String, Integer> results = new HashMap<>();
                    while (rs.next()) {
                        results.put(rs.getString("system_id"), rs.getInt("c"));
                    }
                    return results;
                }
        );
    }

    public Map<String, Integer> getHlvl() {
        return jdbcTemplate.query(
                "SELECT IFNULL(system_id, 'FREE') AS system_id, count(*) AS c " +
                        "FROM account " +
                        "WHERE level >= 30 " +
                        "GROUP BY IFNULL(system_id, 'FREE')",
                (ResultSet rs) -> {
                    HashMap<String, Integer> results = new HashMap<>();
                    while (rs.next()) {
                        results.put(rs.getString("system_id"), rs.getInt("c"));
                    }
                    return results;
                }
        );
    }

    public Map<String, Integer> getWithFlags(String whereClause) {
        return jdbcTemplate.query(
                "SELECT CASE WHEN level < 30 THEN 'worker' ELSE 'hlvl' END AS lvl, count(*) AS c " +
                        "FROM account " +
                        "WHERE " + whereClause + " " +
                        "GROUP BY CASE WHEN level < 30 THEN 'worker' ELSE 'hlvl' END",
                (ResultSet rs) -> {
                    HashMap<String, Integer> results = new HashMap<>();
                    while (rs.next()) {
                        results.put(rs.getString("lvl"), rs.getInt("c"));
                    }
                    return results;
                }
        );
    }

    public Map<String, Integer> getSystemIds() {
        return jdbcTemplate.query(
                "SELECT system_id, MAX(last_modified) AS last_updated " +
                        "FROM account " +
                        "WHERE system_id IS NOT NULL " +
                        "GROUP BY system_id",
                (ResultSet rs) -> {
                    HashMap<String, Integer> results = new HashMap<>();
                    while (rs.next()) {
                        results.put(rs.getString("lvl"), rs.getInt("c"));
                    }
                    return results;
                }
        );
    }

    public List<Instance> getInstanceInfo() {
        return jdbcTemplate.query(
                "SELECT system_id, last_modified, SUM(worker) AS worker, SUM(hlvl) AS hlvl " +
                        "FROM " +
                        "(" +
                        "SELECT " +
                        "system_id, " +
                        "last_modified, " +
                        "CASE WHEN level < 30 THEN 1 ELSE 0 END AS worker, " +
                        "CASE WHEN level < 30 THEN 0 ELSE 1 END AS hlvl " +
                        "FROM account " +
                        "WHERE system_id IS NOT NULL" +
                        ") t " +
                        "GROUP BY system_id",
                (rs, rowNum) -> new Instance(
                        rs.getString("system_id"),
                        rs.getString("last_modified"),
                        rs.getInt("worker"),
                        rs.getInt("hlvl")
                )
        );
    }

    public int releaseInstance(Instance instance) {
        String instanceId = instance.getSystemId();
        return jdbcTemplate.update("UPDATE account SET system_id = NULL WHERE system_id = ?", instanceId);
    }

    public int activateAccounts() {
        jdbcTemplate.update("UPDATE account SET banned = 0 WHERE banned IS NULL;");
        return jdbcTemplate.update("UPDATE account SET shadowbanned = 0 WHERE shadowbanned IS NULL;");
    }

    private List<BlindAccountReport> getTimeoutReport(String whereClause) {
        return jdbcTemplate.query(
                "SELECT count(*) AS count, datediff(now(), last_modified) AS days\n" +
                        "FROM account\n" +
                        "WHERE " + whereClause + " AND last_modified IS NOT NULL " +
                        "GROUP BY datediff(now(), last_modified);",
                (rs, rowNum) -> new BlindAccountReport(
                        rs.getInt("count"),
                        rs.getInt("days")
                )
        );
    }

    private List<BlindAccountReport> getDaysReport(String whereClause) {
        return jdbcTemplate.query(
                "SELECT count(*) AS count, date(last_modified) AS day\n" +
                        "FROM account\n" +
                        "WHERE " + whereClause + " AND last_modified IS NOT NULL " +
                        "GROUP BY date(last_modified);",
                (rs, rowNum) -> new BlindAccountReport(
                        rs.getInt("count"),
                        rs.getDate("day")
                )
        );
    }

    public List<BlindAccountReport> getReport(ChartType chartType, BanType banType, WorkerType workerType) {
        String whereClause = "";
        whereClause = banType.getPredicate() + " AND " + workerType.getPredicate();
        switch (chartType) {
            case DATE:
                return getDaysReport(whereClause);
            case TIMEOUT:
                return getTimeoutReport(whereClause);
        }
        return null;
    }

}
