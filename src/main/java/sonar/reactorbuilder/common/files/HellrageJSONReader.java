package sonar.reactorbuilder.common.files;

import com.google.gson.*;
import net.minecraft.util.JsonUtils;
import org.apache.commons.io.IOUtils;
import scala.swing.Reactor;
import sonar.reactorbuilder.ReactorBuilder;
import sonar.reactorbuilder.common.dictionary.GlobalDictionary;
import sonar.reactorbuilder.common.dictionary.DictionaryEntry;
import sonar.reactorbuilder.common.reactors.templates.AbstractTemplate;
import sonar.reactorbuilder.common.reactors.templates.UnderhaulSFRTemplate;
import sonar.reactorbuilder.util.Translate;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class HellrageJSONReader extends AbstractFileReader {

    public static final HellrageJSONReader INSTANCE = new HellrageJSONReader();

    public String error = "";

    @Nullable
    public AbstractTemplate readTemplate(File file) {
        if(ReactorBuilder.isOverhaul()){
            error = "JSON files are UNDERHAUL only";
            return null;
        }

        error = "";

        Path path = file.toPath();
        BufferedReader reader = null;
        UnderhaulSFRTemplate template = null;

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

            if(!(major==1 && minor==2 && build>=23)){
                error = String.format("Old JSON version %s.%s.%s", major, minor, build);
                return null;
            }

            JsonObject dims = json.getAsJsonObject("InteriorDimensions");
            JsonObject usedFuel = json.getAsJsonObject("UsedFuel");
            String fuelName = usedFuel.get("Name").getAsString();
            String fuelGlobalName = HellrageJSONDictionary.INSTANCE.getGlobalName(fuelName);
            DictionaryEntry fuelInfo = GlobalDictionary.getComponentInfo(fuelGlobalName);
            if(fuelInfo == null){
                ReactorBuilder.logger.error("Missing Fuel Type: " + fuelName);
            }
            UnderhaulSFRTemplate sfr = new UnderhaulSFRTemplate(file.getName(), dims.get("X").getAsInt(), dims.get("Y").getAsInt(), dims.get("Z").getAsInt(), fuelInfo);

            JsonObject compressedReactor = json.getAsJsonObject("CompressedReactor");
            for(Map.Entry<String, JsonElement> elementEntry : compressedReactor.entrySet()){
                String globalName = HellrageJSONDictionary.INSTANCE.getGlobalName(elementEntry.getKey());
                DictionaryEntry info = GlobalDictionary.getComponentInfo(globalName);
                if(info != null){
                    JsonArray array = elementEntry.getValue().getAsJsonArray();
                    for(JsonElement locElement : array){
                        JsonObject locObject = locElement.getAsJsonObject();
                        int x = locObject.get("X").getAsInt()-1;
                        int y = locObject.get("Y").getAsInt()-1;
                        int z = locObject.get("Z").getAsInt()-1;
                        sfr.setComponentInfo(info, x, y, z);
                    }
                }else{
                    ReactorBuilder.logger.error("Missing Component Type: " + elementEntry.getKey());
                }
            }
            template = sfr;

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
}
