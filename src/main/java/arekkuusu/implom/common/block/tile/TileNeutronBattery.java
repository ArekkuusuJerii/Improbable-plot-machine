/*
 * Arekkuusu / Improbable plot machine. 2018
 *
 * This project is licensed under the MIT.
 * The source code is available on github:
 * https://github.com/ArekkuusuJerii/Improbable-plot-machine
 */
package arekkuusu.implom.common.block.tile;

import arekkuusu.implom.api.capability.nbt.IWorldAccessNBTDataCapability;
import arekkuusu.implom.api.capability.InventoryHelper;
import arekkuusu.implom.api.capability.WorldAccessHelper;
import arekkuusu.implom.api.state.Properties;
import arekkuusu.implom.common.block.BlockNeutronBattery;
import arekkuusu.implom.common.handler.data.capability.provider.NeutronProvider;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Created by <Arekkuusu> on 20/03/2018.
 * It's distributed as part of Improbable plot machine.
 */
public class TileNeutronBattery extends TileBase implements INBTDataTransferableImpl {

	public final NeutronProvider wrapper = new NeutronProvider(this);

	public void setActiveLazy(boolean active) {
		IBlockState state = world.getBlockState(getPos());
		boolean wasActive = isActiveLazy();
		if(active != wasActive) {
			world.setBlockState(getPos(), state.withProperty(Properties.ACTIVE, active));
		}
	}

	public boolean isActiveLazy() {
		return getStateValue(Properties.ACTIVE, getPos()).orElse(false);
	}

	public EnumFacing getFacingLazy() {
		return getStateValue(BlockDirectional.FACING, pos).orElse(EnumFacing.DOWN);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return wrapper.hasCapability(capability, facing) || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		return wrapper.hasCapability(capability, facing)
				? wrapper.getCapability(capability, facing)
				: super.getCapability(capability, facing);
	}

	@Override
	void readNBT(NBTTagCompound compound) {
		wrapper.deserializeNBT(compound.getCompoundTag(BlockNeutronBattery.Constants.NBT_NEUTRON));
	}

	@Override
	void writeNBT(NBTTagCompound compound) {
		compound.setTag(BlockNeutronBattery.Constants.NBT_NEUTRON, wrapper.serializeNBT());
	}

	@Override
	void readSync(NBTTagCompound compound) {
		NBTTagCompound tag = compound.getCompoundTag(BlockNeutronBattery.Constants.NBT_NEUTRON);
		wrapper.inventoryInstance.deserializeNBT(tag.getCompoundTag("inventory"));
	}

	@Override
	void writeSync(NBTTagCompound compound) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("inventory", wrapper.inventoryInstance.serializeNBT());
		compound.setTag(BlockNeutronBattery.Constants.NBT_NEUTRON, tag);
	}

	@Override
	public String group() {
		return DefaultGroup.LUMEN;
	}

	@Override
	public void setKey(@Nullable UUID uuid) {
		wrapper.worldAccessInstance.setKey(uuid);
	}

	@Nullable
	@Override
	public UUID getKey() {
		return wrapper.worldAccessInstance.getKey();
	}

	@Override
	public void fromItemStack(ItemStack stack) {
		InventoryHelper.getCapability(stack).map(inv -> inv.getStackInSlot(0)).ifPresent(s -> wrapper.inventoryInstance.setStackInSlot(0, s));
		WorldAccessHelper.getCapability(stack).map(IWorldAccessNBTDataCapability::getKey).ifPresent(wrapper.worldAccessInstance::setKey);
	}

	@Override
	public void toItemStack(ItemStack stack) {
		InventoryHelper.getCapability(stack).ifPresent(instance -> instance.insertItem(0, wrapper.inventoryInstance.getStackInSlot(0), false));
		WorldAccessHelper.getCapability(stack).ifPresent(instance -> instance.setKey(wrapper.worldAccessInstance.getKey()));
	}
}
