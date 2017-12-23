package com.codenameart.pgpooljui

/**
 * Created by Artem on 18.10.2017.
 */
class Account {
    String auth_service
    String username
    String password
    String email
    String last_modified
    String system_id
    Double latitude
    Double longitude

    // from player_stats
    short level
    int xp
    int encounters
    int balls_thrown
    int captures
    int spins
    double walked

    // from get_inbox
    String team
    int coins
    int stardust

    // account health
    boolean warn
    boolean banned
    boolean ban_flag
    String tutorial_state // a CSV-list of tutorial steps completed
    boolean captcha
    int rareless_scans
    boolean shadowbanned

    // inventory info
    short balls
    short total_items
    short pokemon
    short eggs
    short incubators

    Account(String username, short level, boolean shadowbanned, boolean warn, boolean banned, boolean captcha, String last_modified) {
        this.username = username
        this.password = password
        this.level = level
        this.shadowbanned = shadowbanned
        this.warn = warn
        this.banned = banned
        this.captcha = captcha
        this.last_modified = last_modified
    }


}
