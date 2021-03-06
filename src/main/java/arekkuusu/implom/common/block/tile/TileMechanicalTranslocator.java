/*
 * Arekkuusu / Improbable plot machine. 2018
 *
 * This project is licensed under the MIT.
 * The source code is available on github:
 * https://github.com/ArekkuusuJerii/Improbable-plot-machine
 */
package arekkuusu.implom.common.block.tile;

import arekkuusu.implom.api.capability.PositionsHelper;
import arekkuusu.implom.api.capability.data.PositionsNBTData;
import arekkuusu.implom.api.capability.nbt.IPositionsNBTDataCapability;
import arekkuusu.implom.api.helper.FacingHelper;
import arekkuusu.implom.api.state.Properties;
import arekkuusu.implom.client.util.helper.ProfilerHelper;
import arekkuusu.implom.common.handler.data.capability.provider.PositionsDefaultProvider;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static net.minecraft.util.EnumFacing.UP;

/*
 * Created by <Arekkuusu> on 17/01/2018.
 * It's distributed as part of Improbable plot machine.
 */
public class TileMechanicalTranslocator extends TileBase implements INBTDataTransferableImpl {

	public final PositionsDefaultProvider provider = new PositionsDefaultProvider(this);
	public boolean powered;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		boolean refresh = super.shouldRefresh(world, pos, oldState, newState);
		if(!refresh && oldState != newState) {
			int index = provider.positionsNBTDataCapability.index(world, pos, getFacingLazy());
			if(index != -1)
				provider.positionsNBTDataCapability.get().get(index)
						.setFacing(newState.getValue(BlockDirectional.FACING));
		}
		return refresh;
	}

	public void activate() {
		if(!world.isRemote && isActive() && canSend()) {
			ProfilerHelper.begin("[Mechanical Translocator] Relocating block");
			List<PositionsNBTData.Position> list = provider.positionsNBTDataCapability.get();
			if(!list.isEmpty() && list.size() > 1) {
				int index = provider.positionsNBTDataCapability.index(getWorld(), getPos(), getFacingLazy());
				if(index == -1) return;
				PositionsNBTData.Position origin = list.get(index);
				if(origin.getWorld() != null && origin.getPos() != null && origin.getFacing() != null) {
					for(int size = list.size(), i = index + 1; ; i++) {
						if(i >= size) i = 0; //Lööps brøther
						PositionsNBTData.Position destiny = list.get(i);
						if(destiny.equals(origin))
							break; //Cant replace itself brøther
						if(destiny.getWorld() == null
								|| destiny.getPos() == null
								|| destiny.getFacing() == null
								|| !destiny.getWorld().isBlockLoaded(destiny.getPos().offset(destiny.getFacing())))
							continue; //Cant replace invalid pös brøther
						if(isUnbreakable(destiny.getWorld(), destiny.getPos().offset(destiny.getFacing())))
							continue; //Cant replace unbreakable blöcks brøther
						Pair<IBlockState, NBTTagCompound> originTags = getState(
								origin.getWorld(), origin.getPos().offset(origin.getFacing())
						);
						Pair<IBlockState, NBTTagCompound> destinyTags = getState(
								destiny.getWorld(), destiny.getPos().offset(destiny.getFacing())
						);
						setState(destinyTags, origin.getWorld(), origin.getPos().offset(origin.getFacing()), destiny.getFacing(), origin.getFacing());
						setState(originTags, destiny.getWorld(), destiny.getPos().offset(destiny.getFacing()), origin.getFacing(), destiny.getFacing());
					}
				}
			}
			ProfilerHelper.end();
		}
	}

	private boolean isUnbreakable(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return state.getBlockHardness(world, pos) == -1;
	}

	private boolean canSend() {
		BlockPos pos = getPos().offset(getFacingLazy());
		IBlockState state = world.getBlockState(pos);
		return state.getBlockHardness(world, pos) != -1 && !getTile(TileMechanicalTranslocator.class, world, pos).isPresent();
	}

	private Pair<IBlockState, NBTTagCompound> getState(World world, BlockPos pos) {
		NBTTagCompound tag = new NBTTagCompound();
		IBlockState state = world.getBlockState(pos);
		getTile(TileEntity.class, world, pos).ifPresent(tile -> {
			tile.writeToNBT(tag);
			tag.removeTag("x");
			tag.removeTag("y");
			tag.removeTag("z");
		});
		return Pair.of(state, tag);
	}

	private void setState(Pair<IBlockState, NBTTagCompound> data, World world, BlockPos pos, EnumFacing from, EnumFacing to) {
		IBlockState state = data.getLeft();
		getTile(TileEntityLockable.class, world, pos).ifPresent(tile -> world.removeTileEntity(pos));
		world.setBlockState(pos, state.getMaterial() != Material.WATER ? getRotationState(state, from, to) : state);
		getTile(TileEntity.class, world, pos).ifPresent(tile -> {
			NBTTagCompound tag = data.getRight();
			tag.setInteger("x", tile.getPos().getX());
			tag.setInteger("y", tile.getPos().getY());
			tag.setInteger("z", tile.getPos().getZ());
			tile.readFromNBT(tag);
			tile.markDirty();
		});
		world.notifyNeighborsOfStateChange(getPos(), getBlockType(), true);
		world.notifyBlockUpdate(pos, state, state, 16);
	}

	private IBlockState getRotationState(IBlockState original, EnumFacing from, EnumFacing to) {
		ProfilerHelper.begin("[Mechanical Translocator] Rotating block");
		boolean hasProperty = false;
		for(IProperty<?> p : original.getPropertyKeys()) {
			if(p.getValueClass().equals(EnumFacing.class) && p.getName().toLowerCase(Locale.ROOT).contains("facing")) {
				hasProperty = true;
				//noinspection unchecked
				IProperty<EnumFacing> property = (IProperty<EnumFacing>) p;
				EnumFacing actual = original.getValue(property);
				original = apply(property, original, FacingHelper.rotate(actual, from, to));
				break;
			}
		}
		if(!hasProperty) { // uwu
			original = original.withRotation(FacingHelper.getHorizontalRotation(from, to));
		}
		ProfilerHelper.end();
		return original;
	}

	private IBlockState apply(IProperty<EnumFacing> property, IBlockState state, EnumFacing facing) {
		return property.getAllowedValues().contains(facing) ? state.withProperty(property, facing) : state;
	}

	public EnumFacing getFacingLazy() {
		return getStateValue(BlockDirectional.FACING, pos).orElse(UP);
	}

	public boolean isActive() {
		return getStateValue(Properties.ACTIVE, getPos()).orElse(false);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return provider.hasCapability(capability, facing) || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		return provider.hasCapability(capability, facing)
				? provider.getCapability(capability, facing)
				: super.getCapability(capability, facing);
	}

	/* NBT */
	public static final String NBT_PROVIDER = "provider";
	public static final String NBT_POWERED = "powered";

	@Override
	void writeNBT(NBTTagCompound compound) {
		compound.setTag(NBT_PROVIDER, provider.serializeNBT());
		compound.setBoolean(NBT_POWERED, powered);
	}

	@Override
	void readNBT(NBTTagCompound compound) {
		provider.deserializeNBT(compound.getCompoundTag(NBT_PROVIDER));
		powered = compound.getBoolean(NBT_POWERED);
	}

	@Override
	public String group() {
		return DefaultGroup.TRANSLOCATOR;
	}

	@Override
	public void setKey(@Nullable UUID uuid) {
		provider.positionsNBTDataCapability.setKey(uuid);
	}

	@Nullable
	@Override
	public UUID getKey() {
		return provider.positionsNBTDataCapability.getKey();
	}

	@Override
	public void fromItemStack(ItemStack stack) {
		PositionsHelper.getCapability(stack).map(IPositionsNBTDataCapability::getKey).ifPresent(this::setKey);
	}

	@Override
	public void toItemStack(ItemStack stack) {
		PositionsHelper.getCapability(stack).ifPresent(instance -> instance.setKey(this.getKey()));
	}
}
