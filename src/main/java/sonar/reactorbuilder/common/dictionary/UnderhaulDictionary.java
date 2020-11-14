package sonar.reactorbuilder.common.dictionary;

public class UnderhaulDictionary {

    public static final String NC_MODID = "nuclearcraft";

    public static void buildDictionary(){

        //general
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"fuel_cell", NC_MODID, "cell_block",0);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"graphite_moderator", NC_MODID, "ingot_block",8); //TODO SUPPORT FOR OTHER MODS ORE DICTIONARY.
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"beryllium_moderator", NC_MODID,"ingot_block",9);

        //coolers
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"water_cooler", NC_MODID,"cooler",1);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"redstone_cooler", NC_MODID,"cooler",2);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"quartz_cooler", NC_MODID,"cooler",3);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"gold_cooler", NC_MODID,"cooler",4);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"glowstone_cooler", NC_MODID,"cooler",5);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"lapis_cooler", NC_MODID,"cooler",6);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"diamond_cooler", NC_MODID,"cooler",7);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"helium_cooler", NC_MODID,"cooler",8);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"enderium_cooler", NC_MODID,"cooler",9);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"cryotheum_cooler", NC_MODID,"cooler",10);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"iron_cooler", NC_MODID,"cooler",11);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"emerald_cooler", NC_MODID,"cooler",12);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"copper_cooler", NC_MODID,"cooler",13);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"tin_cooler", NC_MODID,"cooler",14);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_COMPONENT,"magnesium_cooler", NC_MODID,"cooler",15);

        ////

        //thorium
        addFuelTypes("tbu", "fuel_thorium", 0);

        //uranium
        addFuelTypes("leu_233", "fuel_uranium", 0);
        addFuelTypes("heu_233", "fuel_uranium", 2);
        addFuelTypes("leu_235", "fuel_uranium", 4);
        addFuelTypes("heu_235", "fuel_uranium", 6);

        //neptunium
        addFuelTypes("len_236", "fuel_neptunium", 0);
        addFuelTypes("hen_236", "fuel_neptunium", 2);

        //plutonium
        addFuelTypes("lep_239", "fuel_plutonium", 0);
        addFuelTypes("hep_239", "fuel_plutonium", 2);
        addFuelTypes("lep_241", "fuel_plutonium", 4);
        addFuelTypes("hep_241", "fuel_plutonium", 6);

        //mixed oxide
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_FUEL, "mox_239", NC_MODID, "fuel_mixed_oxide", 0);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_FUEL, "mox_241", NC_MODID, "fuel_mixed_oxide", 1);

        //americium
        addFuelTypes("lea_242", "fuel_americium", 0);
        addFuelTypes("hea_242", "fuel_americium", 2);

        //curium
        addFuelTypes("lecm_243", "fuel_curium", 0);
        addFuelTypes("hecm_243", "fuel_curium", 2);
        addFuelTypes("lecm_245", "fuel_curium", 4);
        addFuelTypes("hecm_245", "fuel_curium", 6);
        addFuelTypes("lecm_247", "fuel_curium", 8);
        addFuelTypes("hecm_247", "fuel_curium", 10);

        //berkelium
        addFuelTypes("leb_248", "fuel_berkelium", 0);
        addFuelTypes("heb_248", "fuel_berkelium", 2);

        //californium
        addFuelTypes("lecf_249", "fuel_californium", 0);
        addFuelTypes("hecf_249", "fuel_californium", 2);
        addFuelTypes("lecf_251", "fuel_californium", 4);
        addFuelTypes("hecf_251", "fuel_californium", 6);

        //casing
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_CASING_SOLID, "sfr_casing", NC_MODID, "fission_block", 0);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_CASING_GLASS, "sfr_glass", NC_MODID, "reactor_casing_transparent", 0);

    }

    public static void addFuelTypes(String fuelType, String itemName, int meta){
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_FUEL, fuelType, NC_MODID, itemName, meta++);
        GlobalDictionary.addDictionaryItemEntry(DictionaryEntryType.UNDERHAUL_FUEL, fuelType + "_ox", NC_MODID, itemName, meta);
    }


}
