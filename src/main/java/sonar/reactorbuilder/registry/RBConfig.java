package sonar.reactorbuilder.registry;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class RBConfig {

    public static Configuration config;

    public static final String DEFAULT_BUILDER = "Default Reactor Builder";
    public static final String CREATIVE_BUILDER = "Creative Reactor Builder";
    public static final String ADVANCED = "Advanced Settings";

    public static float blocksPerTickDefault;
    public static int energyPerBlockDefault;
    public static int energyCapacityDefault;
    public static int energyTransferDefault;

    public static float blocksPerTickCreative;

    public static boolean allowHellrageJSON;
    public static boolean allowThizNCPF;
    public static boolean allowFuelCellFiltering;
    public static boolean allowFuelVesselFiltering;
    public static boolean allowIrradiatorFiltering;


    public static void init(File file) {
        config = new Configuration(new File(file.getPath(), "reactor_builder.cfg"));
        config.load();
        read();
        config.save();
    }

    public static void read() {
        blocksPerTickDefault = config.getFloat("Blocks per tick", DEFAULT_BUILDER, 0.2F, 0, 64, "");
        energyPerBlockDefault = config.getInt("Energy used per block", DEFAULT_BUILDER, 50, 0, Integer.MAX_VALUE, "");
        energyCapacityDefault = config.getInt("Energy storage capacity", DEFAULT_BUILDER, 50000, 0, Integer.MAX_VALUE, "rf");
        energyTransferDefault = config.getInt("Energy storage transfer", DEFAULT_BUILDER, 3200, 0, Integer.MAX_VALUE, "rf/t");

        blocksPerTickCreative = config.getFloat("Blocks per tick", CREATIVE_BUILDER, 4, 0, 64, "");

        allowHellrageJSON = config.getBoolean("Allow Hellrage JSONs", ADVANCED, true, "");
        allowThizNCPF = config.getBoolean("Allow Thiz NCPF", ADVANCED, true, "");
        allowFuelCellFiltering = config.getBoolean("Allow Fuel Cell Filtering", ADVANCED, true, "");
        allowFuelVesselFiltering = config.getBoolean("Allow Fuel Vessel Filtering", ADVANCED, true, "");
        allowIrradiatorFiltering = config.getBoolean("Allow Irradiator Recipe Filtering", ADVANCED, true, "");
    }
}