package com.example.tomer.baslo.firebasechat;

import java.io.Serializable;

public enum Game implements Serializable {
    LEAGUE_OF_LEGENDS, DOTA2, SMITE, HEROES_OF_THE_STORM, HEROES_OF_NEWERTH;

    @Override
    public String toString() {
        switch(this) {

            case LEAGUE_OF_LEGENDS:
                return "leagueOfLegends";
            case DOTA2:
                return "dota2";
            case SMITE:
                return "smite";
            case HEROES_OF_THE_STORM:
                return "heroesOfTheStorm";
            case HEROES_OF_NEWERTH:
                return "heroesOfNewerth";
            default:
                return "None";

        }
    }
}