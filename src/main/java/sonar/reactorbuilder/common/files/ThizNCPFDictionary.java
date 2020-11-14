package sonar.reactorbuilder.common.files;

public class ThizNCPFDictionary extends AbstractFileDictionary {

    public static final ThizNCPFDictionary INSTANCE = new ThizNCPFDictionary();

    public void buildDictionary(){

        //universal
        add("Fuel Cell", "fuel_cell");




        //// UNDERHAUL \\\\

        //coolers
        add("Water Cooler", "water_cooler");
        add("Redstone Cooler", "redstone_cooler");
        add("Quartz Cooler", "quartz_cooler");
        add("Gold Cooler", "gold_cooler");
        add("Glowstone Cooler", "glowstone_cooler");
        add("Lapis Cooler", "lapis_cooler");
        add("Diamond Cooler", "diamond_cooler");
        add("Helium Cooler", "helium_cooler");
        add("Enderium Cooler", "enderium_cooler");
        add("Cryotheum Cooler", "cryotheum_cooler");
        add("Iron Cooler", "iron_cooler");
        add("Emerald Cooler", "emerald_cooler");
        add("Copper Cooler", "copper_cooler");
        add("Tin Cooler", "tin_cooler");
        add("Magnesium Cooler", "magnesium_cooler");

        add("Active Water Cooler", "water_cooler");
        add("Active Redstone Cooler", "redstone_cooler");
        add("Active Quartz Cooler", "quartz_cooler");
        add("Active Gold Cooler", "gold_cooler");
        add("Active Glowstone Cooler", "glowstone_cooler");
        add("Active Lapis Cooler", "lapis_cooler");
        add("Active Diamond Cooler", "diamond_cooler");
        add("Active Helium Cooler", "helium_cooler");
        add("Active Enderium Cooler", "enderium_cooler");
        add("Active Cryotheum Cooler", "cryotheum_cooler");
        add("Active Iron Cooler", "iron_cooler");
        add("Active Emerald Cooler", "emerald_cooler");
        add("Active Copper Cooler", "copper_cooler");
        add("Active Tin Cooler", "tin_cooler");
        add("Active Magnesium Cooler", "magnesium_cooler");

        add("Graphite", "graphite_moderator");
        add("Beryllium", "beryllium_moderator");




        //// OVERHAUL \\\\

        //// GENERAL \\\\
        add("Fuel Vessel", "fuel_vessel");
        add("Neutron Irradiator", "neutron_irradiator");
        add("Conductor", "conductor");

        //// MODERATORS \\\\
        add("Graphite Moderator", "graphite_moderator");
        add("Beryllium Moderator", "beryllium_moderator");
        add("Heavy Water Moderator", "heavy_water_moderator");

        //// REFLECTORS  \\\\
        add("Beryllium-Carbon Reflector", "beryllium_carbon_reflector");
        add("Lead-Steel Reflector", "lead_steel_reflector");
        add("Boron-Silver Neutron Shield", "boron_silver_neutron_shield");

        //// HEAT SINKS - SFR \\\\
        add("Water Heat Sink", "water_sink");
        add("Iron Heat Sink", "iron_sink");
        add("Redstone Heat Sink", "redstone_sink");
        add("Quartz Heat Sink", "quartz_sink");
        add("Obsidian Heat Sink", "obsidian_sink");
        add("Nether Brick Heat Sink", "nether_brick_sink");
        add("Glowstone Heat Sink", "glowstone_sink");
        add("Lapis Heat Sink", "lapis_sink");
        add("Gold Heat Sink", "gold_sink");
        add("Prismarine Heat Sink", "prismarine_sink");
        add("Slime Heat Sink", "slime_sink");
        add("End Stone Heat Sink", "end_stone_sink");
        add("Purpur Heat Sink", "purpur_sink");
        add("Diamond Heat Sink", "diamond_sink");
        add("Emerald Heat Sink", "emerald_sink");
        add("Copper Heat Sink", "copper_sink");
        add("Tin Heat Sink", "tin_sink");
        add("Lead Heat Sink", "lead_sink");
        add("Boron Heat Sink", "boron_sink");
        add("Lithium Heat Sink", "lithium_sink");
        add("Magnesium Heat Sink", "magnesium_sink");
        add("Manganese Heat Sink", "manganese_sink");
        add("Aluminum Heat Sink", "aluminum_sink");
        add("Silver Heat Sink", "silver_sink");
        add("Fluorite Heat Sink", "fluorite_sink");
        add("Villiaumite Heat Sink", "villiaumite_sink");
        add("Carobbiite Heat Sink", "carobbiite_sink");
        add("Arsenic Heat Sink", "arsenic_sink");
        add("Liquid Nitrogen Heat Sink", "liquid_nitrogen_sink");
        add("Liquid Helium Heat Sink", "liquid_helium_sink");
        add("Enderium Heat Sink", "enderium_sink");
        add("Cryotheum Heat Sink", "cryotheum_sink");

        //// HEATERS - MSR \\\\
        add("Standard Coolant Heater", "standard_heater");
        add("Iron Coolant Heater", "iron_heater");
        add("Redstone Coolant Heater", "redstone_heater");
        add("Quartz Coolant Heater", "quartz_heater");
        add("Obsidian Coolant Heater", "obsidian_heater");
        add("Nether Brick Coolant Heater", "nether_brick_heater");
        add("Glowstone Coolant Heater", "glowstone_heater");
        add("Lapis Coolant Heater", "lapis_heater");
        add("Gold Coolant Heater", "gold_heater");
        add("Prismarine Coolant Heater", "prismarine_heater");
        add("Slime Coolant Heater", "slime_heater");
        add("End Stone Coolant Heater", "end_stone_heater");
        add("Purpur Coolant Heater", "purpur_heater");
        add("Diamond Coolant Heater", "diamond_heater");
        add("Emerald Coolant Heater", "emerald_heater");
        add("Copper Coolant Heater", "copper_heater");
        add("Tin Coolant Heater", "tin_heater");
        add("Lead Coolant Heater", "lead_heater");
        add("Boron Coolant Heater", "boron_heater");
        add("Lithium Coolant Heater", "lithium_heater");
        add("Magnesium Coolant Heater", "magnesium_heater");
        add("Manganese Coolant Heater", "manganese_heater");
        add("Aluminum Coolant Heater", "aluminum_heater");
        add("Silver Coolant Heater", "silver_heater");
        add("Fluorite Coolant Heater", "fluorite_heater");
        add("Villiaumite Coolant Heater", "villiaumite_heater");
        add("Carobbiite Coolant Heater", "carobbiite_heater");
        add("Arsenic Coolant Heater", "arsenic_heater");
        add("Liquid Nitrogen Coolant Heater", "liquid_nitrogen_heater");
        add("Liquid Helium Coolant Heater", "liquid_helium_heater");
        add("Enderium Coolant Heater", "enderium_heater");
        add("Cryotheum Coolant Heater", "cyrotheum_heater");

        //// TURBINE GENERAL \\\\
        add("Dynamo Coil Connector", "coil_connector");
        add("Rotor Bearing", "rotor_bearing");

        //// TURBINE BLADES \\\\
        add("Steel Rotor Blade", "steel_blade");
        add("Extreme Alloy Rotor Blade", "extreme_blade");
        add("SiC-SiC CMC Rotor Blade", "sic_blade");
        add("Rotor Stator", "rotor_stator");

        //// TURBINE COILS \\\\
        add("Magnesium Dynamo Coil", "magnesium_coil");
        add("Beryllium Dynamo Coil", "beryllium_coil");
        add("Aluminum Dynamo Coil", "aluminum_coil");
        add("Gold Dynamo Coil", "gold_coil");
        add("Copper Dynamo Coil", "copper_coil");
        add("Silver Dynamo Coil", "silver_coil");


        //thorium
        addFuelTypes("TBU", "tbu");

        //uranium
        addFuelTypes("LEU-233", "leu_233");
        addFuelTypes("HEU-233", "heu_233");
        addFuelTypes("LEU-235", "leu_235");
        addFuelTypes("HEU-235", "heu_235");

        //neptunium
        addFuelTypes("LEN-236", "len_236");
        addFuelTypes("HEN-236", "hen_236");

        //plutonium
        addFuelTypes("LEP-239", "lep_239");
        addFuelTypes("HEP-239", "hep_239");
        addFuelTypes("LEP-241", "lep_241");
        addFuelTypes("HEP-241", "hep_241");

        //mixed
        add("MTR-239", "mix_239_tr");
        add("MOX-239", "mix_239_ox");
        add("MNI-239", "mix_239_ni");
        add("MZA-239", "mix_239_za");
        add("MF4-239", "mix_239_fl");

        add("MTR-241", "mix_241_tr");
        add("MOX-241", "mix_241_ox");
        add("MNI-241", "mix_241_ni");
        add("MZA-241", "mix_241_za");
        add("MF4-241", "mix_241_fl");

        //americium
        addFuelTypes("LEA-242", "lea_242");
        addFuelTypes("HEA-242", "hea_242");

        //curium
        addFuelTypes("LECm-243", "lecm_243");
        addFuelTypes("HECm-243", "hecm_243");
        addFuelTypes("LECm-245", "lecm_245");
        addFuelTypes("HECm-245", "hecm_245");
        addFuelTypes("LECm-247", "lecm_247");
        addFuelTypes("HECm-247", "hecm_247");

        //berkelium
        addFuelTypes("LEB-248", "leb_248");
        addFuelTypes("HEB-248", "heb_248");

        //californium
        addFuelTypes("LECf-249", "lecf_249");
        addFuelTypes("HECf-249", "hecf_249");
        addFuelTypes("LECf-251", "lecf_251");
        addFuelTypes("HECf-251", "hecf_251");

        add("Thorium to Protactinium-Enriched Thorium", "irradiator_thorium");
        add("Protactinium-Enriched Thorium to Protactinium-233", "irradiator_protactinium");
        add("Bismuth Dust to Polonium Dust", "irradiator_bismuth");
    }

    public void addFuelTypes(String key, String value){
        add(key + " Triso", value + "_tr");
        add(key + " Oxide", value + "_ox");
        add(key + " Nitride", value + "_ni");
        add(key + "-Zirconium Alloy", value + "_za");
        add(key + " Fluoride", value + "_fl");
    }

}
