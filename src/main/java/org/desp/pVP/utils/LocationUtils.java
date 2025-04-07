package org.desp.pVP.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtils {

    public static Location parseLocation(String str) {
        // 예시: "world:pvp, x:-10.5, y:70.0, z:15.2, yaw:90.0, pitch:0.0"
        String[] parts = str.split(", ");
        String world = parts[0].split(":")[1];
        double x = Double.parseDouble(parts[1].split(":")[1]);
        double y = Double.parseDouble(parts[2].split(":")[1]);
        double z = Double.parseDouble(parts[3].split(":")[1]);
        float yaw = Float.parseFloat(parts[4].split(":")[1]);
        float pitch = Float.parseFloat(parts[5].split(":")[1]);

        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

}
