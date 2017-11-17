package com.example.tomer.baslo.firebasechat;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Tomer on 11/11/2017.
 */

@IgnoreExtraProperties
public class User {
    public String uid;
    public String email;
    public String firebaseToken;
    public boolean leagueOfLegends;
    public boolean dota2;
    public boolean smite;
    public boolean heroesOfTheStorm;
    public boolean heroesOfNewerth;

    public User() {
    }

    public User(String uid, String email, String firebaseToken) {
        this.uid = uid;
        this.email = email;
        this.firebaseToken = firebaseToken;
        this.leagueOfLegends = false;
        this.dota2 = false;
        this.smite = false;
        this.heroesOfTheStorm = false;
        this.heroesOfNewerth = false;
    }
}
