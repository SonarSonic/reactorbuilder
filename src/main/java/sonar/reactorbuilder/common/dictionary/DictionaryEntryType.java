package sonar.reactorbuilder.common.dictionary;

public enum DictionaryEntryType {
    UNDERHAUL_COMPONENT(0, false,"underhaul component"),
    UNDERHAUL_FUEL(1, false,"underhaul fuel"),
    UNDERHAUL_CASING_SOLID(2, false,"underhaul solid casing"),
    UNDERHAUL_CASING_GLASS(3, false,"underhaul glass casing"),
    UNDERHAUL_EDGES(4, false, "edges"), //not to be registered, only used within ReactorBuilderTE

    OVERHAUL_COMPONENT(11, true,"overhaul component"),
    OVERHAUL_FUEL(12, true,"overhaul fuel"),
    OVERHAUL_LIQUID_FUEL(13, true,"overhaul liquid fuel"),
    OVERHAUL_CASING_SOLID(14, true,"overhaul solid casing"),
    OVERHAUL_CASING_GLASS(15, true,"overhaul glass casing"),
    OVERHAUL_TURBINE_BLADE(16, true,"turbine blade types"),

    IRRADIATOR_RECIPE(20, true,"irradiator recipes");

    public byte id;
    public boolean isOverhaul;
    public String logName;

    DictionaryEntryType(int id, boolean isOverhaul, String s) {
        this.id = (byte)id;
        this.isOverhaul = isOverhaul;
        this.logName = s;
    }

    public byte getID(){
        return id;
    }

    public static DictionaryEntryType getType(byte id){
        for(DictionaryEntryType type : values()){
            if(type.id == id){
                return type;
            }
        }
        return null;
    }
}
