package org.desp.pVP.utils;

public class MatchUtils {

    public static String getTierFromPoint(int point) {
        if (point >= 120) return "챌린저";
        else if (point >= 100) return "마스터";
        else if (point >= 80) return "다이아";
        else if (point >= 60) return "플레티넘";
        else if (point >= 40) return "골드";
        else if (point >= 20) return "실버";
        else return "브론즈";
    }
}
