package org.desp.pVP.database;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.desp.pVP.PVP;

public class DBConfig {

    public String getMongoConnectionContent(){
        File file = new File(PVP.getInstance().getDataFolder().getPath() + "/config.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        String url = yml.getString("mongodb.url");
        int port = yml.getInt("mongodb.port");
        String address = yml.getString("mongodb.address");

        return String.format("%s%s:%s/PVP", url,address, port);
    }
}
