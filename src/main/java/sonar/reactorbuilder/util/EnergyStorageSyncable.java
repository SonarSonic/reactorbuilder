package sonar.reactorbuilder.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class EnergyStorageSyncable extends EnergyStorage {

    public EnergyStorageSyncable(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public void readFromNBT(NBTTagCompound compound) {
        energy = compound.getInteger("energy");
    }

    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("energy", energy);
    }

    public void readFromBuf(ByteBuf buf) {
        energy = buf.readInt();
    }

    public void writeToBuf(ByteBuf buf) {
        buf.writeInt(energy);
    }

}
