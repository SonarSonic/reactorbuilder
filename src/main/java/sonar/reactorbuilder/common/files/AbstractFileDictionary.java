package sonar.reactorbuilder.common.files;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractFileDictionary {

    private LinkedHashMap<String, String> LOCAL_TO_GLOBAL  = new LinkedHashMap<>();
    private LinkedHashMap<String, String> GLOBAL_TO_LOCAL  = new LinkedHashMap<>();

    public AbstractFileDictionary(){
        buildDictionary();
    }

    public final void add(String localName, String globalName){
        LOCAL_TO_GLOBAL.put(localName, globalName);
        GLOBAL_TO_LOCAL.put(globalName, localName);
    }

    public final String getGlobalName(String localName){
        return LOCAL_TO_GLOBAL.get(localName);
    }

    public final String getGlobalName(Function<String, Boolean> matches){
        for(Map.Entry<String, String> entry : LOCAL_TO_GLOBAL.entrySet()){
            if(matches.apply(entry.getKey())){
                return entry.getValue();
            }
        }
        return null;
    }

    public final String getLocalName(String localName){
        return GLOBAL_TO_LOCAL.get(localName);
    }

    public final String getLocalName(Function<String, Boolean> matches){
        for(Map.Entry<String, String> entry : GLOBAL_TO_LOCAL.entrySet()){
            if(matches.apply(entry.getKey())){
                return entry.getValue();
            }
        }
        return null;
    }

    public abstract void buildDictionary();
}
