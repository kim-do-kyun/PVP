package org.desp.pVP.utils;

public class TierUtils {

    public static String getTierFromMMR(int mmr) {
        if (mmr < 1000) return "Bronze";
        else if (mmr < 1500) return "Silver";
        else if (mmr < 2000) return "Gold";
        else if (mmr < 2500) return "Platinum";
        else if (mmr < 3000) return "Diamond";
        else return "Challenger";
    }
}

