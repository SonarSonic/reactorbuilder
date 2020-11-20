package sonar.reactorbuilder.proxy;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import sonar.reactorbuilder.ReactorBuilder;
import sonar.reactorbuilder.common.ReactorBuilderBlock;
import sonar.reactorbuilder.common.ReactorBuilderTileEntity;
import sonar.reactorbuilder.common.dictionary.GlobalDictionary;
import sonar.reactorbuilder.network.PacketHandler;
import sonar.reactorbuilder.network.PacketSyncDictionary;
import sonar.reactorbuilder.network.templates.TemplateManager;
import sonar.reactorbuilder.registry.RBBlocks;
import sonar.reactorbuilder.registry.RBTab;
import sonar.reactorbuilder.registry.RBConfig;

@Mod.EventBusSubscriber
public class CommonProxy {

    public boolean isOverhaul;
    public int recipeCount;

    public void preInit(FMLPreInitializationEvent e) {
        ///register packets
        ReactorBuilder.logger.info("Registering packets");
        PacketHandler.registerMessages("reactorbuilder");

        ReactorBuilder.logger.info("Loading config");
        RBConfig.init(e.getModConfigurationDirectory());
    }

    public void init(FMLInitializationEvent e) {
        ///register gui handler
        ReactorBuilder.logger.info("Registering gui handler");
        NetworkRegistry.INSTANCE.registerGuiHandler(ReactorBuilder.instance, new GuiProxy());

        addShapedOreRecipe(new ItemStack(RBBlocks.reactorBuilder), "PLP", "SCS", "PLP", 'C', "chassis", 'S', "solenoidCopper", 'P', "plateBasic", 'L', "ingotLead");

    }

    public void addShapedOreRecipe(ItemStack input, Object ...outputs){
        ResourceLocation location = new ResourceLocation(ReactorBuilder.MODID, "recipe" + recipeCount);
        ForgeRegistries.RECIPES.register(new ShapedOreRecipe(location, input, outputs).setRegistryName(location));
        recipeCount++;
    }

    public void postInit(FMLPostInitializationEvent e) {

        boolean isNCLoaded = Loader.isModLoaded("nuclearcraft");
        if(!isNCLoaded){
            ReactorBuilder.logger.info("NuclearCraft isn't loaded!");
            return;
        }
        ModContainer ncContainer = Loader.instance().getIndexedModList().get("nuclearcraft");
        String version = ncContainer.getVersion();
        isOverhaul = version.startsWith("2o");

        ReactorBuilder.logger.info("Detected NuclearCraft Version: {} - Overhaul: {}", version, isOverhaul);

        ///build underhaul block dictionary
        ReactorBuilder.logger.info("Building reactor dictionary");
        GlobalDictionary.initDictionary(isOverhaul);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        ///register blocks
        ReactorBuilder.logger.info("Registering blocks");
        event.getRegistry().register(new ReactorBuilderBlock("reactorbuilder").setCreativeTab(RBTab.INSTANCE));
        event.getRegistry().register(new ReactorBuilderBlock.Creative("creativereactorbuilder").setCreativeTab(RBTab.INSTANCE));

        ///register tile entities
        ReactorBuilder.logger.info("Registering tile entities");
        GameRegistry.registerTileEntity(ReactorBuilderTileEntity.class, new ResourceLocation(ReactorBuilder.MODID, "reactorbuilder"));
        GameRegistry.registerTileEntity(ReactorBuilderTileEntity.Creative.class, new ResourceLocation(ReactorBuilder.MODID, "creativereactorbuilder"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        //register items
        //ReactorBuilder.logger.info("Registering items");
        //event.getRegistry().register(new DesignTemplateItem().setCreativeTab(RBTab.INSTANCE));

        //register block items
        ReactorBuilder.logger.info("Registering blocks");
        event.getRegistry().register(new ItemBlock(RBBlocks.reactorBuilder).setRegistryName(RBBlocks.reactorBuilder.getRegistryName()));
        event.getRegistry().register(new ItemBlock(RBBlocks.creativeReactorBuilder).setRegistryName(RBBlocks.creativeReactorBuilder.getRegistryName()));
    }


    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ReactorBuilder.logger.info("Sending Reactor Dictionary to player: {}", event.player.getGameProfile().getName());
        PacketHandler.INSTANCE.sendTo(new PacketSyncDictionary(), (EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public static void clientDisconnection(FMLNetworkEvent.ClientDisconnectionFromServerEvent event){
        TemplateManager.getTemplateManager(true).clear();
    }

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }
        TemplateManager.getDownloadHandler(true).tick();
    }


    @SubscribeEvent
    public static void onServerTick(final TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }
        TemplateManager.getDownloadHandler(false).tick();
    }

    public EntityPlayer getPlayer(MessageContext ctx) {
        return ctx.getServerHandler().player;
    }

}
