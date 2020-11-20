package sonar.reactorbuilder.common.dictionary;

public class OverhaulDictionary {

    public static final String NC_MODID = "nuclearcraft";

    public static void buildDictionary(){

        ///COMPONENTS

        ///general
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "boron_silver_neutron_shield", NC_MODID, "fission_shield", 0);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "conductor", NC_MODID, "fission_conductor", 0);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "neutron_irradiator", NC_MODID, "fission_irradiator", 0);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "fuel_cell", NC_MODID, "solid_fission_cell", 0);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "fuel_vessel", NC_MODID, "salt_fission_vessel", 0);

        ///moderators
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "graphite_moderator", NC_MODID, "ingot_block", 8);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "beryllium_moderator", NC_MODID, "ingot_block", 9);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "heavy_water_moderator", NC_MODID, "heavy_water_moderator", 0);

        ///reflectors
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "beryllium_carbon_reflector", NC_MODID, "fission_reflector", 0);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "lead_steel_reflector", NC_MODID, "fission_reflector", 1);

        /// heat sinks - sfr
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "water_sink", NC_MODID, "solid_fission_sink", 0);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "iron_sink", NC_MODID, "solid_fission_sink", 1);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "redstone_sink", NC_MODID, "solid_fission_sink", 2);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "quartz_sink", NC_MODID, "solid_fission_sink", 3);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "obsidian_sink", NC_MODID, "solid_fission_sink", 4);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "nether_brick_sink", NC_MODID, "solid_fission_sink", 5);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "glowstone_sink", NC_MODID, "solid_fission_sink", 6);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "lapis_sink", NC_MODID, "solid_fission_sink", 7);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "gold_sink", NC_MODID, "solid_fission_sink", 8);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "prismarine_sink", NC_MODID, "solid_fission_sink", 9);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "slime_sink", NC_MODID, "solid_fission_sink", 10);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "end_stone_sink", NC_MODID, "solid_fission_sink", 11);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "purpur_sink", NC_MODID, "solid_fission_sink", 12);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "diamond_sink", NC_MODID, "solid_fission_sink", 13);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "emerald_sink", NC_MODID, "solid_fission_sink", 14);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "copper_sink", NC_MODID, "solid_fission_sink", 15);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "tin_sink", NC_MODID, "solid_fission_sink2", 0);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "lead_sink", NC_MODID, "solid_fission_sink2", 1);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "boron_sink", NC_MODID, "solid_fission_sink2", 2);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "lithium_sink", NC_MODID, "solid_fission_sink2", 3);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "magnesium_sink", NC_MODID, "solid_fission_sink2", 4);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "manganese_sink", NC_MODID, "solid_fission_sink2", 5);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "aluminum_sink", NC_MODID, "solid_fission_sink2", 6);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "silver_sink", NC_MODID, "solid_fission_sink2", 7);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "fluorite_sink", NC_MODID, "solid_fission_sink2", 8);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "villiaumite_sink", NC_MODID, "solid_fission_sink2", 9);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "carobbiite_sink", NC_MODID, "solid_fission_sink2", 10);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "arsenic_sink", NC_MODID, "solid_fission_sink2", 11);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "liquid_nitrogen_sink", NC_MODID, "solid_fission_sink2", 12);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "liquid_helium_sink", NC_MODID, "solid_fission_sink2", 13);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "enderium_sink", NC_MODID, "solid_fission_sink2", 14);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "cryotheum_sink", NC_MODID, "solid_fission_sink2", 15);

        ///heaters - msr
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "standard_heater", NC_MODID, "salt_fission_heater", 0);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "iron_heater", NC_MODID, "salt_fission_heater", 1);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "redstone_heater", NC_MODID, "salt_fission_heater", 2);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "quartz_heater", NC_MODID, "salt_fission_heater", 3);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "obsidian_heater", NC_MODID, "salt_fission_heater", 4);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "nether_brick_heater", NC_MODID, "salt_fission_heater", 5);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "glowstone_heater", NC_MODID, "salt_fission_heater", 6);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "lapis_heater", NC_MODID, "salt_fission_heater", 7);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "gold_heater", NC_MODID, "salt_fission_heater", 8);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "prismarine_heater", NC_MODID, "salt_fission_heater", 9);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "slime_heater", NC_MODID, "salt_fission_heater", 10);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "end_stone_heater", NC_MODID, "salt_fission_heater", 11);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "purpur_heater", NC_MODID, "salt_fission_heater", 12);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "diamond_heater", NC_MODID, "salt_fission_heater", 13);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "emerald_heater", NC_MODID, "salt_fission_heater", 14);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "copper_heater", NC_MODID, "salt_fission_heater", 15);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "tin_heater", NC_MODID, "salt_fission_heater2", 0);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "lead_heater", NC_MODID, "salt_fission_heater2", 1);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "boron_heater", NC_MODID, "salt_fission_heater2", 2);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "lithium_heater", NC_MODID, "salt_fission_heater2", 3);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "magnesium_heater", NC_MODID, "salt_fission_heater2", 4);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "manganese_heater", NC_MODID, "salt_fission_heater2", 5);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "aluminum_heater", NC_MODID, "salt_fission_heater2", 6);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "silver_heater", NC_MODID, "salt_fission_heater2", 7);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "fluorite_heater", NC_MODID, "salt_fission_heater2", 8);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "villiaumite_heater", NC_MODID, "salt_fission_heater2", 9);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "carobbiite_heater", NC_MODID, "salt_fission_heater2", 10);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "arsenic_heater", NC_MODID, "salt_fission_heater2", 11);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "liquid_nitrogen_heater", NC_MODID, "salt_fission_heater2", 12);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "liquid_helium_heater", NC_MODID, "salt_fission_heater2", 13);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "enderium_heater", NC_MODID, "salt_fission_heater2", 14);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "cyrotheum_heater", NC_MODID, "salt_fission_heater2", 15);


        //// TURBINE GENERAL \\\\
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "coil_connector", NC_MODID, "turbine_coil_connector", 0);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "rotor_bearing", NC_MODID, "turbine_rotor_bearing", 0);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_TURBINE_BLADE, "rotor_shaft", NC_MODID, "turbine_rotor_shaft", 0, true);

        //// TURBINE BLADES \\\\
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_TURBINE_BLADE, "steel_blade", NC_MODID, "turbine_rotor_blade_steel", 0, true);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_TURBINE_BLADE, "extreme_blade", NC_MODID, "turbine_rotor_blade_extreme", 0, true);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_TURBINE_BLADE, "sic_blade", NC_MODID, "turbine_rotor_blade_sic_sic_cmc", 0, true);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_TURBINE_BLADE, "rotor_stator", NC_MODID, "turbine_rotor_stator", 0, true);

        //// TURBINE COILS \\\\
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "magnesium_coil", NC_MODID, "turbine_dynamo_coil", 0);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "beryllium_coil", NC_MODID, "turbine_dynamo_coil", 1);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "aluminum_coil", NC_MODID, "turbine_dynamo_coil", 2);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "gold_coil", NC_MODID, "turbine_dynamo_coil", 3);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "copper_coil", NC_MODID, "turbine_dynamo_coil", 4);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_COMPONENT, "silver_coil", NC_MODID, "turbine_dynamo_coil", 5);

        //// CASINGS \\\\
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_CASING_SOLID, "sfr_casing", NC_MODID, "fission_casing", 0);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_CASING_GLASS, "sfr_glass", NC_MODID, "fission_glass", 0);

        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_CASING_SOLID, "turbine_casing", NC_MODID, "turbine_casing", 0);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_CASING_GLASS, "turbine_glass", NC_MODID, "turbine_glass", 0);



        ///FUELS

        //thorium
        addFuelTypes("tbu", "fuel_thorium", 0);

        //uranium
        addFuelTypes("leu_233", "fuel_uranium", 0);
        addFuelTypes("heu_233", "fuel_uranium", 4);
        addFuelTypes("leu_235", "fuel_uranium", 8);
        addFuelTypes("heu_235", "fuel_uranium", 12);

        //neptunium
        addFuelTypes("len_236", "fuel_neptunium", 0);
        addFuelTypes("hen_236", "fuel_neptunium", 4);

        //plutonium
        addFuelTypes("lep_239", "fuel_plutonium", 0);
        addFuelTypes("hep_239", "fuel_plutonium", 4);
        addFuelTypes("lep_241", "fuel_plutonium", 8);
        addFuelTypes("hep_241", "fuel_plutonium", 12);

        ///mixed
        addFuelTypes("mix_239", "fuel_mixed", 0);
        addFuelTypes("mix_241", "fuel_mixed", 4);

        //americium
        addFuelTypes("lea_242", "fuel_americium", 0);
        addFuelTypes("hea_242", "fuel_americium", 4);

        //curium
        addFuelTypes("lecm_243", "fuel_curium", 0);
        addFuelTypes("hecm_243", "fuel_curium", 4);
        addFuelTypes("lecm_245", "fuel_curium", 8);
        addFuelTypes("hecm_245", "fuel_curium", 12);
        addFuelTypes("lecm_247", "fuel_curium", 16);
        addFuelTypes("hecm_247", "fuel_curium", 20);

        //berkelium
        addFuelTypes("leb_248", "fuel_berkelium", 0);
        addFuelTypes("heb_248", "fuel_berkelium", 4);

        //californium
        addFuelTypes("lecf_249", "fuel_californium", 0);
        addFuelTypes("hecf_249", "fuel_californium", 4);
        addFuelTypes("lecf_251", "fuel_californium", 8);
        addFuelTypes("hecf_251", "fuel_californium", 12);

        //// IRRADIATOR RECIPES \\\\
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.IRRADIATOR_RECIPE, "irradiator_thorium", NC_MODID, "dust", 3);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.IRRADIATOR_RECIPE, "irradiator_protactinium", NC_MODID, "fission_dust", 3);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.IRRADIATOR_RECIPE, "irradiator_bismuth", NC_MODID, "fission_dust", 0);
    }

    public static void addFuelTypes(String fuelType, String itemName, int meta){
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_FUEL, fuelType + "_tr", NC_MODID, itemName, meta++);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_FUEL, fuelType + "_ox", NC_MODID, itemName, meta++);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_FUEL, fuelType + "_ni", NC_MODID, itemName, meta++);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.OVERHAUL_FUEL, fuelType + "_za", NC_MODID, itemName, meta);

        GlobalDictionary.addDictionaryFluidEntry(DictionaryEntryType.OVERHAUL_LIQUID_FUEL, fuelType + "_fl", fuelType + "_fluoride_flibe");
    }

}
