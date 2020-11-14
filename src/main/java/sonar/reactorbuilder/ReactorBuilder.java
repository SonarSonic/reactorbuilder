package sonar.reactorbuilder;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;
import sonar.reactorbuilder.proxy.CommonProxy;

@Mod(modid = ReactorBuilder.MODID, name = ReactorBuilder.NAME, version = ReactorBuilder.VERSION, useMetadata = true)
public class ReactorBuilder
{
    public static final String MODID = "reactorbuilder";
    public static final String NAME = "Reactor Builder";
    public static final String VERSION = "1.0.0";

    @SidedProxy(clientSide = "sonar.reactorbuilder.proxy.ClientProxy", serverSide = "sonar.reactorbuilder.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static ReactorBuilder instance;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {}

    public static boolean isOverhaul(){
        return proxy.isOverhaul;
    }
}
