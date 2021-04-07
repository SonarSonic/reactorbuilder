package sonar.reactorbuilder.common.files;

public class HellrageJSONDictionary extends AbstractFileDictionary {

    public static HellrageJSONDictionary INSTANCE = new HellrageJSONDictionary();

        @Override
        public void buildDictionary() {
            //general
            add("FuelCell", "fuel_cell");
            add("ReactorCell", "reactor_cell");

            add("Graphite", "graphite_moderator");
            add("Beryllium", "beryllium_moderator");

            //coolers
            add("Water", "water_cooler");
            add("Redstone", "redstone_cooler");
            add("Quartz", "quartz_cooler");
            add("Gold", "gold_cooler");
            add("Glowstone", "glowstone_cooler");
            add("Lapis", "lapis_cooler");
            add("Diamond", "diamond_cooler");
            add("Helium", "helium_cooler");
            add("Enderium", "enderium_cooler");
            add("Cryotheum", "cryotheum_cooler");
            add("Iron", "iron_cooler");
            add("Emerald", "emerald_cooler");
            add("Copper", "copper_cooler");
            add("Tin", "tin_cooler");
            add("Magnesium", "magnesium_cooler");

            ////

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

            //mixed oxide
            add("MOX-239", "mox_239");
            add("MOX-241", "mox_241");

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
        }

    public void addFuelTypes(String key, String value){
        add(key , value);
        add(key + " Oxide", value + "_ox");
    }
    
}
