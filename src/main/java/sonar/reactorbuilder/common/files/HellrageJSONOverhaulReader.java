package sonar.reactorbuilder.common.files;

import com.google.gson.*;
import net.minecraft.util.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.core.Core;
import scala.util.parsing.json.JSON;
import scala.util.parsing.json.JSONObject;
import sonar.reactorbuilder.ReactorBuilder;
import sonar.reactorbuilder.common.dictionary.DictionaryEntry;
import sonar.reactorbuilder.common.dictionary.GlobalDictionary;
import sonar.reactorbuilder.common.reactors.templates.AbstractTemplate;
import sonar.reactorbuilder.common.reactors.templates.OverhaulFissionTemplate;
import sonar.reactorbuilder.common.reactors.templates.UnderhaulSFRTemplate;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class HellrageJSONOverhaulReader extends AbstractFileReader {

    public static final HellrageJSONOverhaulReader INSTANCE = new HellrageJSONOverhaulReader();

    public String error = "";

    @Nullable
    public AbstractTemplate readTemplate(File file) {
        error = "";

        Path path = file.toPath();
        BufferedReader reader = null;
        AbstractTemplate template = null;

        try{
            reader = Files.newBufferedReader(path);
            String contents = IOUtils.toString(reader);
            JsonObject json = JsonUtils.gsonDeserialize(new Gson(), contents, JsonObject.class);

            if(json == null){
                error = "Failed to deserialize JSON";
                return null;
            }

            JsonObject saveVersion = json.getAsJsonObject("SaveVersion");
            int major = saveVersion.get("Major").getAsInt();
            int minor = saveVersion.get("Minor").getAsInt();
            int build = saveVersion.get("Build").getAsInt();

            if(!(major==2 && minor==1 && build>=1)){
                error = String.format("Old JSON version %s.%s.%s", major, minor, build);
                return null;
            }
            JsonObject data = json.getAsJsonObject("Data");
            JsonObject dims = data.getAsJsonObject("InteriorDimensions");


            boolean isMSR = true;
            JsonObject testFuelCells = data.getAsJsonObject("FuelCells");
            for(Map.Entry<String, JsonElement> name : testFuelCells.entrySet()){
                if(!name.getKey().startsWith("[F4]")){
                    isMSR = false;
                    break;
                }
            }

            OverhaulFissionTemplate fissionTemplate = isMSR ? new OverhaulFissionTemplate.MSR(file.getName(), dims.get("X").getAsInt(), dims.get("Y").getAsInt(), dims.get("Z").getAsInt()) : new OverhaulFissionTemplate.SFR(file.getName(), dims.get("X").getAsInt(), dims.get("Y").getAsInt(), dims.get("Z").getAsInt());



            ///coolant heaters
            JsonObject heatSinks = data.getAsJsonObject("HeatSinks");
            for(Map.Entry<String, JsonElement> heatSink : heatSinks.entrySet()){
                DictionaryEntry entry = isMSR ? getMatchingCoolantHeater(heatSink.getKey()) : getMatchingHeatSink(heatSink.getKey());
                setDictionaryEntryFromCoordinates(entry, fissionTemplate, heatSink.getValue().getAsJsonArray());
            }

            ///moderators
            JsonObject moderators = data.getAsJsonObject("Moderators");
            for(Map.Entry<String, JsonElement> moderator : moderators.entrySet()){
                DictionaryEntry entry = getMatchingModerator(moderator.getKey());
                setDictionaryEntryFromCoordinates(entry, fissionTemplate, moderator.getValue().getAsJsonArray());
            }

            ///conductors
            DictionaryEntry conductorEntry = GlobalDictionary.getComponentInfo("conductor");
            JsonArray conductors = data.getAsJsonArray("Conductors");
            setDictionaryEntryFromCoordinates(conductorEntry, fissionTemplate, conductors);

            JsonObject reflectors = data.getAsJsonObject("Reflectors");
            for(Map.Entry<String, JsonElement> reflector : reflectors.entrySet()){
                DictionaryEntry entry = getMatchingReflector(reflector.getKey());
                setDictionaryEntryFromCoordinates(entry, fissionTemplate, reflector.getValue().getAsJsonArray());
            }

            //neutron shields
            JsonObject neutronShields = data.getAsJsonObject("NeutronShields");
            for(Map.Entry<String, JsonElement> neutronShield : neutronShields.entrySet()){
                DictionaryEntry entry = getMatchingNeutronShield(neutronShield.getKey());
                setDictionaryEntryFromCoordinates(entry, fissionTemplate, neutronShield.getValue().getAsJsonArray());
            }

            DictionaryEntry irradiatorEntry = GlobalDictionary.getComponentInfo("neutron_irradiator");

            JsonObject irradiators = data.getAsJsonObject("Irradiators");
            for(Map.Entry<String, JsonElement> irradiator : irradiators.entrySet()){
                setDictionaryEntryFromCoordinates(irradiatorEntry, fissionTemplate, irradiator.getValue().getAsJsonArray());
                /* TODO IRRADIATOR RECIPES
                try{
                    JSON.JSONObject recipe = JSON.parse(name);
                    for(multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe irr : Core.configuration.overhaul.fissionMSR.allIrradiatorRecipes){
                        if(irr.heat==recipe.getFloat("HeatPerFlux")&&irr.efficiency==recipe.getFloat("EfficiencyMultiplier"))irrecipe = irr;
                    }
                }catch(IOException ex){
                    throw new IllegalArgumentException("Invalid irradiator recipe: "+name);
                }
                */
            }

            DictionaryEntry fuelVesselEntry = GlobalDictionary.getComponentInfo("fuel_vessel");
            DictionaryEntry fuelCellEntry = GlobalDictionary.getComponentInfo("fuel_cell");

            JsonObject fuelCells = data.getAsJsonObject("FuelCells");
            for(Map.Entry<String, JsonElement> fuelCell : fuelCells.entrySet()){
                String[] fuelSettings = fuelCell.getKey().split(";");
                String fuelName = fuelSettings[0];
                boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);

                DictionaryEntry fuel = getMatchingFuel(fuelName);
                if(fuel != null){
                    setFuelRecipeFromCoordinates(fuel, fissionTemplate, fuelCell.getValue().getAsJsonArray());
                }else{
                    ReactorBuilder.logger.error("Missing Fuel Type: " + fuelName);
                }

                setDictionaryEntryFromCoordinates(isMSR ? fuelVesselEntry : fuelCellEntry, fissionTemplate, fuelCell.getValue().getAsJsonArray());

                /* TODO SOURCES
                multiblock.configuration.overhaul.fissionmsr.Source src = null;
                if(hasSource){
                    String sourceName = fuelSettings[2];
                    if(sourceName.equals("Self"))hasSource = false;
                    else{
                        for(multiblock.configuration.overhaul.fissionmsr.Source scr : Core.configuration.overhaul.fissionMSR.allSources){
                            if(scr.name.equalsIgnoreCase(sourceName))src = scr;
                        }
                        if(src==null)throw new IllegalArgumentException("Unknown source: "+name);
                    }
                }
                */

                template = fissionTemplate;
            }


        }
        catch (JsonParseException jsonparseexception){
            error = "Error parsing JSON - see log";
            ReactorBuilder.logger.error("Error parsing JSON" + path.toString(), jsonparseexception);
            return null;
        }
        catch (IOException ioexception){
            error = "Error reading JSON - see log";
            ReactorBuilder.logger.error("Error reading reactor file" + path + " from " + file, ioexception);
            return null;
        }
        finally{
            IOUtils.closeQuietly(reader);
        }

        return template;
    }

    public void setDictionaryEntryFromCoordinates(DictionaryEntry entry, AbstractTemplate template, JsonArray array){
        for(Object blok : array){
            JsonObject blockLoc = (JsonObject) blok;
            int x = blockLoc.get("X").getAsInt()-1;
            int y = blockLoc.get("Y").getAsInt()-1;
            int z = blockLoc.get("Z").getAsInt()-1;
            template.setComponentInfo(entry, x, y, z);
        }
    }

    public void setFuelRecipeFromCoordinates(DictionaryEntry fuel, OverhaulFissionTemplate template, JsonArray array){
        template.recipeToIndexMap.putIfAbsent(fuel, new ArrayList<>());
        for(Object blok : array){
            JsonObject blockLoc = (JsonObject) blok;
            int x = blockLoc.get("X").getAsInt()-1;
            int y = blockLoc.get("Y").getAsInt()-1;
            int z = blockLoc.get("Z").getAsInt()-1;
            template.recipeToIndexMap.get(fuel).add(template.getIndexFromInternalPos(x, y, z));
        }
    }


    ///LAZY HELLRAGE to THIZ DICTIONARY....

    public DictionaryEntry getMatchingHeatSink(String localName){
        String globalName = ThizNCPFDictionary.INSTANCE.getGlobalName((thizName) ->
                {
                    if(!thizName.contains("Heat Sink")){
                        return false;
                    }
                    String testName = thizName.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("heatsink", "").replace("liquid", "");
                    String local = localName.toLowerCase(Locale.ENGLISH).replace(" ", "");
                    return testName.equalsIgnoreCase(local);
                }
        );
        return GlobalDictionary.getComponentInfo(globalName);
    }

    public DictionaryEntry getMatchingCoolantHeater(String localName){
        String globalName = ThizNCPFDictionary.INSTANCE.getGlobalName((thizName) ->
            {
                if(!thizName.contains("Coolant Heater")){
                    return false;
                }
                String testName = thizName.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("coolant", "").replace("heater", "").replace("liquid", "");
                String local = localName.toLowerCase(Locale.ENGLISH).replace("water", "standard").replace(" ", "");
                return testName.equalsIgnoreCase(local);
            }
        );
        return GlobalDictionary.getComponentInfo(globalName);
    }

    public DictionaryEntry getMatchingModerator(String localName){
        String globalName = ThizNCPFDictionary.INSTANCE.getGlobalName((thizName) ->
            {
                if(!thizName.contains("Moderator")){
                    return false;
                }
                String testName = thizName.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("moderator", "");
                String local = localName.replace(" ", "");
                return testName.equalsIgnoreCase(local);
            }
        );
        return GlobalDictionary.getComponentInfo(globalName);
    }

    public DictionaryEntry getMatchingReflector(String localName){
        String globalName = ThizNCPFDictionary.INSTANCE.getGlobalName((thizName) ->
                {
                    if(!thizName.contains("Reflector")){
                        return false;
                    }

                    String testName = thizName.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("reflector", "");
                    String local = localName.replace(" ", "");
                    return testName.equalsIgnoreCase(local);
                }
        );
        return GlobalDictionary.getComponentInfo(globalName);
    }

    public DictionaryEntry getMatchingNeutronShield(String localName){
        String globalName = ThizNCPFDictionary.INSTANCE.getGlobalName((thizName) ->
                {
                    if(!thizName.contains("Neutron Shield")){
                        return false;
                    }
                    String testName = thizName.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("neutronshield", "").replace("shield", "");
                    String local = localName.replace(" ", "");
                    return testName.equalsIgnoreCase(local);
                }
        );
        return GlobalDictionary.getComponentInfo(globalName);
    }

    public DictionaryEntry getMatchingFuel(String localName){
        String globalName = ThizNCPFDictionary.INSTANCE.getGlobalName((thizName) ->
                {

                    if(thizName.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(localName.substring(4).replace(" ", ""))){
                        return true;
                    };
                    String appendName = localName;
                    if(localName.startsWith("[OX]"))appendName = appendName.substring(4)+" Oxide";
                    if(localName.startsWith("[NI]"))appendName = appendName.substring(4)+" Nitride";
                    if(localName.startsWith("[ZA]"))appendName = appendName.substring(4)+"-Zirconium Alloy";
                    if(localName.startsWith("[F4]")) appendName = appendName.substring(4)+" Fluoride";
                    return thizName.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(appendName.replace(" ", ""));
                }
        );
        return GlobalDictionary.getComponentInfo(globalName);
    }
}
