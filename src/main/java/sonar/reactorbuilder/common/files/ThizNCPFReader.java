package sonar.reactorbuilder.common.files;

import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
import sonar.reactorbuilder.ReactorBuilder;
import sonar.reactorbuilder.common.dictionary.DictionaryEntry;
import sonar.reactorbuilder.common.dictionary.GlobalDictionary;
import sonar.reactorbuilder.common.reactors.templates.AbstractTemplate;
import sonar.reactorbuilder.common.reactors.templates.OverhaulFissionTemplate;
import sonar.reactorbuilder.common.reactors.templates.OverhaulTurbine;
import sonar.reactorbuilder.common.reactors.templates.UnderhaulSFRTemplate;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ThizNCPFReader extends AbstractFileReader {

    public static final ThizNCPFReader INSTANCE = new ThizNCPFReader();

    public String error = "";

    @Nullable
    @Override
    public AbstractTemplate readTemplate(File file) {
        error = "";

        InputStream in;
        AbstractTemplate template = null;
        try{
            in = Files.newInputStream(file.toPath());
            Config header = Config.newConfig();
            header.load(in);

            int version = header.getByte("version");
            if(version < 7){
                error = String.format("Old NCPF version %s", version-1);
                return null;
            }

            int count = header.get("count");
            if(count <= 0){
                error = "NCPF doesn't contain any multiblocks";
                return null;
            }

            Config config = Config.newConfig();
            config.load(in);

            Config underhaulConfig = config.getConfig("underhaul");
            Config underhaulSFRConfig = underhaulConfig.getConfig("fissionSFR");
            Map<Integer, DictionaryEntry> underhaulSFRBlockMap = buildConfigDictionaryMap(underhaulSFRConfig.getConfigList("blocks"));
            Map<Integer, DictionaryEntry> underhaulSFRFuelMap = buildConfigDictionaryMap(underhaulSFRConfig.getConfigList("fuels"));

            Config overhaulConfig = config.getConfig("overhaul");
            Config overhaulSFRConfig = overhaulConfig.getConfig("fissionSFR");
            Map<Integer, DictionaryEntry> overhaulSFRBlockMap = buildConfigDictionaryMap(overhaulSFRConfig.getConfigList("blocks"));
            Map<Integer, DictionaryEntry> overhaulSFRFuelMap = buildConfigDictionaryMap(overhaulSFRConfig.getConfigList("fuels"));
            Map<Integer, DictionaryEntry> overhaulSFRRecipeMap = buildConfigDictionaryMap(overhaulSFRConfig.getConfigList("irradiatorRecipes"));

            Config overhaulMSRConfig = overhaulConfig.getConfig("fissionMSR");
            Map<Integer, DictionaryEntry> overhaulMSRBlockMap = buildConfigDictionaryMap(overhaulMSRConfig.getConfigList("blocks"));
            Map<Integer, DictionaryEntry> overhaulMSRFuelMap = buildConfigDictionaryMap(overhaulMSRConfig.getConfigList("fuels"));
            Map<Integer, DictionaryEntry> overhaulMSRRecipeMap = buildConfigDictionaryMap(overhaulMSRConfig.getConfigList("irradiatorRecipes"));

            Config overhaulTurbineConfig = overhaulConfig.getConfig("turbine");
            Map<Integer, DictionaryEntry> overhaulTurbineBladesMap = buildConfigDictionaryMap(overhaulTurbineConfig.getConfigList("blades"));
            Map<Integer, DictionaryEntry> overhaulTurbineCoilsMap = buildConfigDictionaryMap(overhaulTurbineConfig.getConfigList("coils"));

            boolean invalidVersion = false;

            for(int i = 0; i < count; i++){
                Config data = Config.newConfig();
                data.load(in);

                int id = data.get("id");
                switch(id){
                    case 0:
                        if(ReactorBuilder.isOverhaul()){
                            invalidVersion = true;
                            break;
                        }
                        ConfigNumberList size = data.get("size");
                        DictionaryEntry fuel = underhaulSFRFuelMap.get((int)data.getByte("fuel", (byte)-1));
                        UnderhaulSFRTemplate underhaulSFR = new UnderhaulSFRTemplate(file.getName(), (int)size.get(0),(int)size.get(1),(int)size.get(2),fuel);

                        readComponentsFromConfig(underhaulSFR, underhaulSFRBlockMap, data.get("blocks"), data.get("compact"));

                        template = underhaulSFR;
                        break;
                    case 1:
                        if(!ReactorBuilder.isOverhaul()){
                            invalidVersion = true;
                            break;
                        }
                        size = data.get("size");
                        OverhaulFissionTemplate overhaulSFRTemplate = new OverhaulFissionTemplate.SFR(file.getName(), (int)size.get(0),(int)size.get(1),(int)size.get(2));

                        readComponentsFromConfig(overhaulSFRTemplate, overhaulSFRBlockMap, data.get("blocks"), data.get("compact"));
                        readRecipeMapFromConfig(overhaulSFRTemplate, overhaulSFRFuelMap, overhaulSFRRecipeMap, data.get("fuels"), data.get("irradiatorRecipes"));

                        template = overhaulSFRTemplate;
                        break;
                    case 2:
                        if(!ReactorBuilder.isOverhaul()){
                            invalidVersion = true;
                            break;
                        }
                        size = data.get("size");
                        OverhaulFissionTemplate overhaulMSRTemplate = new OverhaulFissionTemplate.MSR(file.getName(), (int)size.get(0),(int)size.get(1),(int)size.get(2));

                        readComponentsFromConfig(overhaulMSRTemplate, overhaulMSRBlockMap, data.get("blocks"), data.get("compact"));
                        readRecipeMapFromConfig(overhaulMSRTemplate, overhaulMSRFuelMap, overhaulMSRRecipeMap, data.get("fuels"), data.get("irradiatorRecipes"));

                        template = overhaulMSRTemplate;
                        break;
                    case 3:
                        if(!ReactorBuilder.isOverhaul()){
                            invalidVersion = true;
                            break;
                        }
                        size = data.get("size");
                        // ncpf.configuration.overhaul.turbine.allRecipes.get(data.get("recipe", (byte)-1))
                        OverhaulTurbine overhaulTurbine = new OverhaulTurbine(file.getName(), (int)size.get(0), (int)size.get(1), (int)size.get(2));
                        /*
                        if(data.hasProperty("inputs")){
                            overhaulTurbinePostLoadInputsMap.put(overhaulTurbine, new ArrayList<>());
                            ConfigNumberList inputs = data.get("inputs");
                            for(Number number : inputs.iterable()){
                                overhaulTurbinePostLoadInputsMap.get(overhaulTurbine).add(number.intValue());
                            }
                        }

                         */
                        DictionaryEntry turbineShaft = GlobalDictionary.getComponentInfo("rotor_shaft");
                        ConfigNumberList coils = data.get("coils");
                        int index = 0;
                        for(int z = 0; z < 2; z++){
                            for(int x = 0; x<overhaulTurbine.xSize; x++){
                                for(int y = 0; y<overhaulTurbine.ySize; y++){

                                    int bid = (int) coils.get(index);
                                    if(bid > 0){
                                        DictionaryEntry componentInfo = overhaulTurbineCoilsMap.get(bid-1);
                                        overhaulTurbine.setCoilExact(componentInfo, x, y, z);

                                        if(z == 0 && componentInfo.globalName.equals("rotor_bearing")){
                                            for(int shaftPos = 1; shaftPos < overhaulTurbine.zSize-1; shaftPos++){
                                                overhaulTurbine.setComponentInfo(turbineShaft, x, y, shaftPos);
                                            }
                                        }
                                    }

                                    index++;
                                }
                            }
                        }
                        ConfigNumberList blades = data.get("blades");
                        index = 0;
                        for(int z = 1; z<overhaulTurbine.zSize-1; z++){
                            int bid = (int) blades.get(index);
                            if(bid>0){
                                DictionaryEntry componentInfo = overhaulTurbineBladesMap.get(bid-1);
                                overhaulTurbine.setBladeExact(componentInfo, z);
                            }
                            index++;
                        }

                        template = overhaulTurbine;
                        break;
                    case 4:
                        ///FUSION REACTOR
                        error = "Fusion reactors haven't been added to NC yet";
                        break;
                }
            }
            if(template == null){
                if(invalidVersion){
                    error = "Template for : " + (ReactorBuilder.isOverhaul() ? "Underhaul" : "Overhaul");
                    return null;
                }
            }


        }catch (Exception e){
            ReactorBuilder.logger.error("Error reading reactor file" + file.toPath(), e);
        }
        return template;
    }


    public static Map<Integer, DictionaryEntry> buildConfigDictionaryMap(ConfigList configMap){
        Map<Integer, DictionaryEntry> overhaulSFRFuelMap = new HashMap<>();
        if(configMap != null){
            for(int i = 0; i < configMap.size(); i++){
                Config block = configMap.getConfig(i);
                String localName = block.getString("name");
                String globalName = ThizNCPFDictionary.INSTANCE.getGlobalName(localName);
                overhaulSFRFuelMap.put(i, GlobalDictionary.getComponentInfo(globalName));
            }
        }
        return overhaulSFRFuelMap;
    }

    public static void readComponentsFromConfig(AbstractTemplate template, Map<Integer, DictionaryEntry> blockMap, ConfigNumberList blocks, boolean compact){
        if(compact){
            int index = 0;
            for(int x = 0; x < template.xSize; x++){
                for(int y = 0; y < template.ySize; y++){
                    for(int z = 0; z < template.zSize; z++){
                        int bid = (int) blocks.get(index);
                        if(bid > 0){
                            DictionaryEntry componentInfo = blockMap.get(bid-1);
                            template.setComponentInfo(componentInfo, x, y, z);
                        }
                        index++;
                    }
                }
            }
        }else{
            for(int j = 0; j < blocks.size(); j += 4){
                int x = (int) blocks.get(j);
                int y = (int) blocks.get(j + 1);
                int z = (int) blocks.get(j + 2);
                int bid = (int) blocks.get(j + 3);
                DictionaryEntry componentInfo = blockMap.get(bid-1);
                template.setComponentInfo(componentInfo, x, y, z);
            }
        }
    }

    public static void readRecipeMapFromConfig(OverhaulFissionTemplate template, Map<Integer, DictionaryEntry> fuelMap, Map<Integer, DictionaryEntry> recipeMap, ConfigNumberList fuels, ConfigNumberList irradiatorRecipes){
        int fuelIndex = 0;
        int recipeIndex = 0;

        int index = 0;
        for(int x = 0; x < template.xSize; x ++){
            for(int y = 0; y < template.ySize; y ++){
                for(int z = 0; z < template.zSize; z ++){
                    DictionaryEntry info = template.getComponent(x, y, z);

                    ///fuel filters
                    if(info != null && (info.globalName.equals("fuel_cell") || info.globalName.equals("fuel_vessel"))){
                        DictionaryEntry fuel = fuelMap.get((int)fuels.get(fuelIndex));

                        if(fuel != null){
                            template.recipeToIndexMap.putIfAbsent(fuel, new ArrayList<>());
                            template.recipeToIndexMap.get(fuel).add(index);
                        }

                        fuelIndex++;
                    }

                    ///irradiator recipes
                    if(info != null && info.globalName.equals("neutron_irradiator")){
                        DictionaryEntry recipe = recipeMap.get((int)irradiatorRecipes.get(recipeIndex)-1);

                        if(recipe != null){
                            template.recipeToIndexMap.putIfAbsent(recipe, new ArrayList<>());
                            template.recipeToIndexMap.get(recipe).add(index);
                        }
                        recipeIndex++;
                    }

                    index++;
                }
            }
        }
    }

}
