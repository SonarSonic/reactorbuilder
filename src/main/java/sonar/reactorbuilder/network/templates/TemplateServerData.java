package sonar.reactorbuilder.network.templates;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;
import sonar.reactorbuilder.ReactorBuilder;
import sonar.reactorbuilder.common.reactors.templates.AbstractTemplate;

import java.util.HashMap;
import java.util.Map;

public class TemplateServerData extends WorldSavedData {

    private static final String TEMPLATE_DATA = ReactorBuilder.MODID + "data";
    private static String TEMPLATE_LIST = "templates";

    private static TemplateServerData data;

    public Map<Integer, AbstractTemplate> templates = new HashMap<>();
    private int nextID;

    public TemplateServerData(String key){
        super(key);
    }


    public TemplateServerData() {
        super(TEMPLATE_DATA);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        nextID = compound.getInteger("nextID");

        NBTTagList list = compound.getTagList(TEMPLATE_LIST, Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < list.tagCount() ; i ++){
            NBTTagCompound templateTag = list.getCompoundTagAt(i);
            AbstractTemplate template = AbstractTemplate.readTemplateFromNBT(templateTag);
            if(template != null){
                templates.put(template.templateID, template);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("nextID", nextID);

        NBTTagList list = new NBTTagList();

        for(Map.Entry<Integer, AbstractTemplate> entry : templates.entrySet()){
            NBTTagCompound templateTag = new NBTTagCompound();
            AbstractTemplate.writeTemplateToNBT(templateTag, entry.getValue());
            list.appendTag(templateTag);
        }
        compound.setTag(TEMPLATE_LIST, list);
        return compound;
    }

    public int getNextID(){
        return nextID++;
    }

    //// LOADING & UNLOADING \\\\

    public static TemplateServerData get() {
        if (data == null) {
            load();
        }
        return data;
    }

    private static void load() {
        World world = DimensionManager.getWorld(0);
        MapStorage mapStorage = world.getMapStorage();
        TemplateServerData savedData = (TemplateServerData) mapStorage.getOrLoadData(TemplateServerData.class, TEMPLATE_DATA);
        if(savedData == null){
            savedData = new TemplateServerData();
            mapStorage.setData(TEMPLATE_DATA, savedData);
        }
        data = savedData;
        ReactorBuilder.logger.info("Template Data has loaded {} templates", data.templates.size());
    }

    public static void release() {
        if (data != null) {
            data = null;
            ReactorBuilder.logger.info("Template Data has been unloaded");
        }
    }
}
