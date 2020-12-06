package sonar.reactorbuilder.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sonar.reactorbuilder.common.dictionary.DictionaryEntry;
import sonar.reactorbuilder.common.reactors.templates.AbstractTemplate;
import sonar.reactorbuilder.common.reactors.EnumCasingConfig;
import sonar.reactorbuilder.common.reactors.templates.UnderhaulSFRTemplate;
import sonar.reactorbuilder.network.EnumSyncPacket;
import sonar.reactorbuilder.network.PacketHandler;
import sonar.reactorbuilder.network.PacketTileSync;
import sonar.reactorbuilder.network.templates.TemplateManager;
import sonar.reactorbuilder.network.templates.TemplateServerData;
import sonar.reactorbuilder.registry.RBConfig;
import sonar.reactorbuilder.util.EnergyStorageSyncable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//TODO MORE INTELLIGENT DESTROY PASS + MORE INTELLIGENT BLOCK DETECTION. i.e. ignoring blocks added after building...
public class ReactorBuilderTileEntity extends TileEntity implements ITickable {

    ///saved values
    public EnergyStorageSyncable energyStorage = new EnergyStorageSyncable(RBConfig.energyCapacityDefault, RBConfig.energyTransferDefault);
    public int templateID  = -1;

    //process - saved
    public boolean isBuilding = false;
    public boolean isDestroying = false;
    public byte buildPass = 0;

    //progress - saved
    public int xPos, yPos, zPos;
    public int[] passProgress = new int[1];
    public int destroyProgress = 0;

    ///error - saved
    public String error = "";
    public BlockPos errorPosition = null;

    ///local values - not saved
    private float loopTick;
    private BlockPos startPos = null;

    ///client values - not saved
    //public AbstractTemplate importedTemplate = null;
    public int page;
    public float scroll;

    @Override
    public void update() {
        if(template == null && templateID != -1){
            initTemplate();
            return;
        }
        if(world.isRemote){
            return;
        }
        if(template == null){
            //sanity check - needed if people uninstall NC while a builder was building
            if(isDestroying || isBuilding){
                reset();
            }
            return;
        }

        if(isDestroying){
            destroy();
            markDirty();
            return;
        }

        if(isBuilding){
            if(!template.isCustomPass(buildPass)){
                build();
            }else{
                template.tickCustomPass(this, buildPass);
                if(getPassProgress() == getPassTotal()){
                    nextPass();
                }
            }

            markDirty();
        }
    }

    //// BUILDING CONTROLS \\\\\

    public boolean canBuild(){
        return template != null && checkBuildArea();
    }

    public boolean canDestroy(){
        return template != null;
    }

    public void startBuilding(){
        if(isDestroying){
            isDestroying = false;
            reset();
        }
        if(isBuilding){
            isBuilding = false;
        }else if(canBuild()){
            isBuilding = true;
            reset();
        }
    }

    public void startDestroying(){
        if(isBuilding){
            isBuilding = false;
            reset();
        }
        if(isDestroying){
            isDestroying = false;
        }else if(canDestroy()){
            isDestroying = true;
            reset();
        }
    }

    public void pauseWithError(BlockPos errorPosition, String error){
        this.errorPosition = errorPosition;
        this.isBuilding = false;
        this.isDestroying = false;
        this.error = error;

        //so the reactor builder renderer knows the error position
        sendPacketToNearby(EnumSyncPacket.GENERAL_SYNC, 64);
    }

    public void reset(){
        buildPass = 0;
        xPos = -1;
        yPos = -1;
        zPos = -1;
        passProgress = new int[template != null ? template.getBuildPasses() : 1];
        error = "";
        errorPosition = null;
        destroyProgress = 0;
    }

    public void nextPass(){
        if(buildPass == template.getBuildPasses()-1){
            isBuilding = false;
        }else{
            buildPass++;
            xPos = -1;
            yPos = -1;
            zPos = -1;
        }
    }


    //// BUILDING LOOP \\\\

    public float getBlocksPerTick(){
        return RBConfig.blocksPerTickDefault;
    }

    public void build(){
        loopTick += getBlocksPerTick();
        int place = (int)Math.floor(loopTick);
        if(place >= 1){
            loopTick-=place;
            for(int i = 0; i < place; i ++){
                placeComponent();
                if(!isBuilding){ //breaks the loop after errors or if the build is finished
                    return;
                }
            }
        }
    }

    public void placeComponent(){

        DictionaryEntry component = template.getComponent(xPos, yPos, zPos);
        BlockPos nextPos = getStartPos().add(xPos, yPos, zPos);

        if(component != null && template.canPlaceThisPass(buildPass, xPos, yPos, zPos, component) && !isMatchingComponentAtPos(component, nextPos)){

            ///check target position
            if(!canPlaceComponentAtPos(component, nextPos)){
                pauseWithError(nextPos, String.format("Invalid block %sx: %s, y: %s, z: %s", TextFormatting.GOLD, nextPos.getX(), nextPos.getY(), nextPos.getZ()));
                return;
            }

            if(shouldUseEnergy() && !hasRequiredEnergy()){
                pauseWithError(null, "Not enough energy");
                return;
            }

            if(shouldUseItems()){
                ///check adjacent inventory
                IItemHandler inventory = getAdjacentInventory();
                if(inventory == null){
                    pauseWithError(null,"No inventory detected");
                    return;
                }

                ///check inventory contents
                int targetSlot = getSlotContainingComponent(inventory, component);
                if(targetSlot == -1){
                    pauseWithError(null, String.format("Missing %s%s", TextFormatting.GOLD, component.getItemStack().getDisplayName()));
                    return;
                }

                ///extract inventory item
                inventory.extractItem(targetSlot, 1, false);
            }

            if(shouldUseEnergy()){
                energyStorage.extractEnergy(getEnergyPerBlock(), false);
            }

            ///set component blockstate
            world.setBlockState(nextPos, component.getBlockState(), 2);

            ///trigger place event!
            onComponentPlaced(component, nextPos);
        }

        ///increment the block position
        if(!incrementBuildPos()){
            //if no increments remain move to the next pass
            nextPass();
        }

    }

    /**moves the builder to the next block pos which needs a component*/
    public boolean incrementBuildPos(){

        for(; yPos <= template.ySize; yPos++){
            for(; xPos <= template.xSize; xPos++){
                for(; zPos <= template.zSize; zPos++){
                    DictionaryEntry info = template.getComponent(xPos, yPos, zPos);
                    BlockPos nextPos = getStartPos().add(xPos, yPos, zPos);
                    if(info != null && template.canPlaceThisPass(buildPass, xPos, yPos, zPos, info)){
                        if(!isMatchingComponentAtPos(info, nextPos)){
                            return true;
                        }else{
                            passProgress[buildPass]++;
                        }
                    }
                }
                zPos = -1;
            }
            xPos = -1;
        }
        return false;
    }




    //// DESTROYING LOOP \\\\

    public void destroy(){
        loopTick += getBlocksPerTick();
        int place = (int)Math.floor(loopTick);
        if(place >= 1){
            loopTick-=place;
            for(int i = 0; i < place; i ++){
                destroyComponent();
                if(!isDestroying){ //breaks the loop after errors or if the build is finished
                    return;
                }
            }
        }
    }

    public void destroyComponent(){
        DictionaryEntry component = template.getComponent(xPos, yPos, zPos);
        BlockPos nextPos = getStartPos().add(xPos, yPos, zPos);
        if(component != null && isMatchingComponentAtPos(component, nextPos)){

            if(shouldUseEnergy() && !hasRequiredEnergy()){
                pauseWithError(null, "Not enough energy");
                return;
            }

            IBlockState state = world.getBlockState(nextPos);
            NonNullList<ItemStack> drops = NonNullList.create();
            state.getBlock().getDrops(drops, world, nextPos, state, 0);

            if(shouldUseItems()){
                ///check adjacent inventory
                IItemHandler inventory = getAdjacentInventory();
                if(inventory == null){
                    pauseWithError(null,"No inventory detected");
                    return;
                }

                ///check inventory space
                for(ItemStack drop : drops){
                    ItemStack insert = ItemHandlerHelper.insertItem(inventory, drop, true);
                    if(!insert.isEmpty()){
                        pauseWithError(null,"Inventory too full");
                        return;
                    }
                }

                ///add dropped items to inventory
                for(ItemStack drop : drops){
                    ItemHandlerHelper.insertItem(inventory, drop, false);
                }
            }

            if(shouldUseEnergy()){
                energyStorage.extractEnergy(getEnergyPerBlock(), false);
            }

            ///set component blockstate
            world.setBlockState(nextPos, Blocks.AIR.getDefaultState(), 2);

            ///trigger destroy event!
            onComponentDestroyed(component, nextPos);
        }

        ///increment the block position
        if(!incrementDestroyPos()){
            //if no increments remain stop the destruction!
            reset();
            isDestroying = false;
        }

    }

    /**moves the builder to the next block pos which needs a component*/
    public boolean incrementDestroyPos(){
        for(; yPos <= template.ySize; yPos++){
            for(; xPos <= template.xSize; xPos++){
                for(; zPos <= template.zSize; zPos++){
                    DictionaryEntry info = template.getComponent(xPos, yPos, zPos);
                    BlockPos nextPos = getStartPos().add(xPos, yPos, zPos);
                    if(info != null){
                        if(isMatchingComponentAtPos(info, nextPos)){
                            return true;
                        }else{
                            destroyProgress++;
                        }
                    }
                }
                zPos = -1;
            }
            xPos = -1;
        }
        return false;
    }


    /// progress

    public int getProgress(){
        if(template == null){
            return 0;
        }

        if(isDestroying){
            return destroyProgress;
        }

        int progress = 0;
        for(int i = 0; i < passProgress.length; i ++){
            progress += passProgress[i];
        }
        return progress;
    }

    public int getTotalProgress(){
        if(template ==  null){
            return 0;
        }

        if(isDestroying){
            return template.totalSolidComponents + template.totalEdges + template.totalSolidCasing + template.totalGlassCasing;
        }

        int total = 0;
        for(int i = 0; i < template.getBuildPasses(); i ++){
            total+=template.getBuildPassTotal(i);
        }
        return total;
    }


    public int getPassProgress(){
        if(template ==  null){
            return 0;
        }
        return passProgress[buildPass];
    }

    public int getPassTotal(){
        if(template ==  null){
            return 0;
        }
        return template.getBuildPassTotal(buildPass);
    }

    public String getPassName(){
        if(template == null){
            return "";
        }
        return template.getBuildPassNames()[buildPass];
    }


    public void onComponentPlaced(DictionaryEntry component, BlockPos pos){
        world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 0.5F);
    }


    public void onComponentDestroyed(DictionaryEntry component, BlockPos pos){
        world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1.0F, 0.5F);
    }



    //// POSITIONS \\\\

    public boolean canPlaceComponentAtPos(@Nullable DictionaryEntry component, BlockPos pos){
        if(component != null){
            return component.canPlaceComponentAtPos(world, pos);
        }
        return world.isAirBlock(pos) || world.getBlockState(pos).getBlock() instanceof BlockLiquid || world.getBlockState(pos).getBlock() instanceof IFluidBlock;
    }

    public boolean isMatchingComponentAtPos(@Nullable DictionaryEntry component, BlockPos pos){
        if(component != null){
            return component.isMatchingComponentAtPos(world, pos);
        }
        return world.isAirBlock(pos);
    }

    public BlockPos getStartPos(){
        if(startPos != null){
            return startPos;
        }
        return startPos = template.getStartPos(this);
    }

    public boolean checkBuildArea(){
        for(int testY = template.getExtStart().getY(); testY <= template.getExtEnd().getY(); testY++){
            for(int testX = template.getExtStart().getX(); testX <= template.getExtEnd().getX(); testX++){
                for(int testZ = template.getExtStart().getZ(); testZ <= template.getExtEnd().getZ(); testZ++){
                    BlockPos pos = getStartPos().add(testX, testY, testZ);
                    DictionaryEntry info = template.getComponent(testX, testY, testZ);
                    if(!canPlaceComponentAtPos(info, pos) && !isMatchingComponentAtPos(info, pos)){
                        pauseWithError(pos, String.format("Invalid block %sx: %s, y: %s, z: %s", TextFormatting.GOLD, pos.getX(), pos.getY(), pos.getZ()));
                        return false;
                    }
                }
            }
        }
        return true;
    }



    //// INVENTORIES \\\\

    public boolean shouldUseItems(){
        return true;
    }

    @Nullable
    public IItemHandler getAdjacentInventory(){
        BlockPos invPos = getPos().offset(EnumFacing.UP);
        TileEntity tile = world.getTileEntity(invPos);
        if(tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN)){
            return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
        }
        return null;
    }

    public int getSlotContainingComponent(IItemHandler inventory, DictionaryEntry component){
        int targetSlot = -1;
        for(int slot = 0; slot < inventory.getSlots(); slot++){
            ItemStack stack = inventory.getStackInSlot(slot);
            if(ItemStack.areItemsEqual(stack, component.getItemStack()) && ItemStack.areItemStackTagsEqual(stack, component.getItemStack())){
                if(!inventory.extractItem(slot, 1, true).isEmpty()){
                    targetSlot = slot;
                }
                break;
            }
        }
        return targetSlot;
    }


    //// ENERGY \\\\

    public boolean shouldUseEnergy(){
        return true;
    }

    public int getEnergyPerBlock(){
        return RBConfig.energyPerBlockDefault;
    }

    public boolean hasRequiredEnergy(){
        return energyStorage.getEnergyStored() >= getEnergyPerBlock();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityEnergy.ENERGY){
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityEnergy.ENERGY){
            return (T) energyStorage;
        }
        return super.getCapability(capability, facing);
    }



    //// TEMPLATES \\\\

    public AbstractTemplate template = null;

    public void initTemplate(){
        startPos = null;
        template = TemplateManager.getTemplateManager(world.isRemote).getTemplate(templateID);
        if(!world.isRemote && template == null){
            templateID = -1;
        }
        if(template != null){
            template.updateAdditionalInfo();
            template.sortAdditionalInfo();
        }
    }

    public void changeTemplate(int templateID){
        if(this.templateID == templateID){
            return;
        }
        removeTemplateData();
        this.templateID = templateID;

        if(!this.world.isRemote){
            sendPacketToNearby(EnumSyncPacket.SYNC_TEMPLATE, 32);
            markDirty(); //saving isn't automated as it might be in SonarCore.
        }

        initTemplate();
    }

    public void removeTemplateData(){
        if(world.isRemote){
            return;
        }
        //REMOVE THE OLD TEMPLATE FROM DATA.
        TemplateServerData.get().templates.remove(templateID);
        TemplateServerData.get().markDirty();
    }


    //// SAVING \\\\

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        isBuilding = compound.getBoolean("building");
        isDestroying = compound.getBoolean("destroy");
        xPos = compound.getInteger("xPos");
        yPos = compound.getInteger("yPos");
        zPos = compound.getInteger("zPos");
        passProgress = compound.getIntArray("progress");
        destroyProgress = compound.getInteger("destroyP");
        error = compound.getString("error");
        energyStorage.readFromNBT(compound);

        if(compound.hasKey("errorPos")){
            errorPosition = BlockPos.fromLong(compound.getLong("errorPos"));
        }else{
            errorPosition = null;
        }

        templateID = compound.getInteger("templateID");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("building", isBuilding);
        compound.setBoolean("destroy", isDestroying);
        compound.setInteger("xPos", xPos);
        compound.setInteger("yPos", yPos);
        compound.setInteger("zPos", zPos);
        compound.setIntArray("progress", passProgress);
        compound.setInteger("destroyP", destroyProgress);
        compound.setString("error", error);
        energyStorage.writeToNBT(compound);

        if(errorPosition != null){
            compound.setLong("errorPos", errorPosition.toLong());
        }
        compound.setInteger("templateID", templateID);
        return compound;
    }


    //// PACKETS \\\\

    public void sendPacketToNearby(EnumSyncPacket type, int range){
        NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), range);
        PacketHandler.INSTANCE.sendToAllAround(new PacketTileSync(this, type), point);
    }

    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 8, this.getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    public void writeSyncPacket(ByteBuf buf, EnumSyncPacket type) {
        switch (type){
            case GENERAL_SYNC:{
                buf.writeBoolean(isBuilding);
                buf.writeBoolean(isDestroying);
                buf.writeByte(buildPass);
                buf.writeInt(xPos);
                buf.writeInt(yPos);
                buf.writeInt(zPos);
                buf.writeInt(destroyProgress);

                buf.writeInt(passProgress.length);
                for(int i = 0; i < passProgress.length; i ++){
                    buf.writeInt(passProgress[i]);
                }


                ByteBufUtils.writeUTF8String(buf, error);
                energyStorage.writeToBuf(buf);

                buf.writeBoolean(errorPosition != null);
                if(errorPosition != null){
                    buf.writeLong(errorPosition.toLong());
                }
                break;
            }
            case SYNC_TEMPLATE:{
                buf.writeInt(templateID);
                buf.writeBoolean(template != null);
                if(template != null){
                    template.writeHeaderToBuf(buf);
                }
                break;
            }
            case TOGGLE_BUILDING:{
                ///
                break;
            }
            case SYNC_CASING_TYPES:{
                for(int i = 0; i < EnumCasingConfig.values().length; i++){
                    buf.writeBoolean(template.caseConfig[i]);
                }
                if(template instanceof UnderhaulSFRTemplate){
                    ByteBufUtils.writeItemStack(buf, ((UnderhaulSFRTemplate) template).edgeItem);
                }
                break;
            }
        }
    }

    public void readSyncPacket(ByteBuf buf, EnumSyncPacket type) {
        switch (type){
            case GENERAL_SYNC:{
                isBuilding = buf.readBoolean();
                isDestroying = buf.readBoolean();
                buildPass = buf.readByte();
                xPos = buf.readInt();
                yPos = buf.readInt();
                zPos = buf.readInt();
                destroyProgress = buf.readInt();

                int passes = buf.readInt();
                passProgress = new int[passes];
                for(int i = 0; i < passes; i ++){
                    passProgress[i] = buf.readInt();
                }

                error = ByteBufUtils.readUTF8String(buf);
                energyStorage.readFromBuf(buf);

                if(buf.readBoolean()){
                    errorPosition = BlockPos.fromLong(buf.readLong());
                }else{
                    errorPosition = null;
                }
                break;
            }
            case SYNC_TEMPLATE: {
                templateID = buf.readInt();
                initTemplate();
                if(buf.readBoolean() && template !=null){
                    template.readHeaderFromBuf(buf);
                    template.updateAdditionalInfo();
                    template.sortAdditionalInfo();
                }
                break;
            }
            case TOGGLE_BUILDING:{
                startBuilding();
                break;
            }
            case TOGGLE_DESTROYING:{
                startDestroying();
                break;
            }
            case SYNC_CASING_TYPES:{
                for(int i = 0; i < EnumCasingConfig.values().length; i++){
                    template.caseConfig[i] = buf.readBoolean();
                }
                if(template instanceof UnderhaulSFRTemplate){
                    ((UnderhaulSFRTemplate) template).edgeItem = ByteBufUtils.readItemStack(buf);
                }
                template.updateAdditionalInfo();
                template.sortAdditionalInfo();

                ///TEMPLATE SETTINGS ALTERED!!
                TemplateServerData.get().markDirty();
                break;
            }
        }
    }



    //// INTERACTION \\\\

    public boolean canInteractWith(EntityPlayer playerIn) {
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }


    //// RENDERING \\\\

    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }


    public static class Creative extends ReactorBuilderTileEntity{

        public Creative(){}

        @Override
        public boolean shouldUseItems() {
            return false;
        }

        @Override
        public boolean shouldUseEnergy() {
            return false;
        }

        @Override
        public float getBlocksPerTick() {
            return RBConfig.blocksPerTickCreative;
        }
    }

}