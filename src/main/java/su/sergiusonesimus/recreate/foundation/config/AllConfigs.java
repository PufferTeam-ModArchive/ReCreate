package su.sergiusonesimus.recreate.foundation.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class AllConfigs {

    public static CClient CLIENT;
    public static CCommon COMMON;
    public static CServer SERVER;

    public static void init(File configFile) {
        Configuration config = new Configuration(configFile);

        CClient.init(config);
        CCommon.init(config);
        CServer.init(config);

        if (config.hasChanged()) {
            config.save();
        }
    }

}
