package sonar.reactorbuilder;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.Logger;
import sonar.reactorbuilder.network.templates.TemplateManager;
import sonar.reactorbuilder.network.templates.TemplateServerData;
import sonar.reactorbuilder.proxy.CommonProxy;

@Mod(modid = ReactorBuilder.MODID, name = ReactorBuilder.NAME, version = ReactorBuilder.VERSION, useMetadata = true)
public class ReactorBuilder
{
    public static final String MODID = "reactorbuilder";
    public static final String NAME = "Reactor Builder";
    public static final String VERSION = "1.0.1";

    @SidedProxy(clientSide = "sonar.reactorbuilder.proxy.ClientProxy", serverSide = "sonar.reactorbuilder.proxy.CommonProxy")
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

    @Mod.EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        TemplateServerData.release();
        TemplateManager.getTemplateManager(false).clear();
        TemplateManager.getTemplateManager(true).clear();
    }


    public static boolean isOverhaul(){
        return proxy.isOverhaul;
    }
}
