/*******************************************************************************
 * Arekkuusu / Solar 2018
 *
 * This project is licensed under the MIT.
 * The source code is available on github:
 * https://github.com/ArekkuusuJerii/Solar#solar
 ******************************************************************************/
package arekkuusu.solar.common.block.tile;

import arekkuusu.solar.client.effect.FXUtil;
import arekkuusu.solar.common.block.ModBlocks;
import arekkuusu.solar.common.block.fluid.ModFluids;
import net.katsstuff.mirror.client.particles.GlowTexture;
import net.katsstuff.mirror.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Created by <Arekkuusu> on 4/5/2018.
 * It's distributed as part of Solar.
 */
public class TileFissionInducer extends TileBase implements ITickable {

	private static final BlockPos[] POSITIONS = {
			new BlockPos(1, 0, 1 ),
			new BlockPos(-1, 0, -1 ),
			new BlockPos(1, 0, 0 ),
			new BlockPos(0, 0, 1 ),
			new BlockPos(-1, 0, 0 ),
			new BlockPos(0, 0, -1 ),
			new BlockPos(-1, 0, 1 ),
			new BlockPos(1, 0, -1 )
	};
	public static final int MAX_CAPACITY = 10000;
	private int[] ticks = new int[POSITIONS.length];
	private final FluidTank handler;
	private int tick;

	public TileFissionInducer() {
		handler = new FluidTank(new FluidStack(ModFluids.GOLD, 0), MAX_CAPACITY);
		Arrays.fill(ticks, -1);
	}

	@Override
	public void update() {
		if(!world.isRemote) {
			if(tick++ % 20 == 0 && canFill()) {
				for(int i = 0; i < POSITIONS.length; i++) {
					BlockPos position = POSITIONS[i].add(getPos());
					IBlockState state = world.getBlockState(position);
					if(state.getBlock() == Blocks.GOLD_BLOCK) {
						if(ticks[i]++ >= 10) {
							world.playEvent(2001, position, Block.getStateId(state));
							world.setBlockState(position, ModBlocks.MOLTEN_GOLD.getDefaultState());
							ticks[i] = -1;
						}
					} else if(state.getBlock() == ModBlocks.MOLTEN_GOLD) {
						if(ticks[i]++ >= 25) {
							world.playEvent(2001, position, Block.getStateId(state));
							FluidStack stack = new FluidStack(ModFluids.GOLD, Fluid.BUCKET_VOLUME);
							((IFluidBlock) ModFluids.GOLD.getBlock()).place(world, position, stack, true);
							ticks[i] = -1;
						}
					} else if(state.getBlock() instanceof IFluidBlock && world.rand.nextFloat() < 0.25F) {
						IFluidBlock f = (IFluidBlock) state.getBlock();
						if(f.getFluid() == ModFluids.GOLD && f.canDrain(world, position)) {
							FluidStack stack = f.drain(world, position, false);
							if(stack != null && stack.amount == Fluid.BUCKET_VOLUME) {
								handler.fill(f.drain(world, position, true), true);
							}
						}
					}
				}
			}
			applyGravity();
		} else for(BlockPos position : POSITIONS) {
			IBlockState state = world.getBlockState(position.add(getPos()));
			if(state.getBlock() == Blocks.GOLD_BLOCK || state.getBlock() == ModBlocks.MOLTEN_GOLD) {
				Vector3 pos = new Vector3.WrappedVec3i(position.add(getPos())).asImmutable().add(Math.random(), 0.75, Math.random());
				Vector3 speed = Vector3.apply(0, 0.05, 0).multiply(world.rand.nextFloat());
				int color = world.rand.nextBoolean() ? 0xff5000 : 0xfff200;
				FXUtil.spawnTunneling(world, pos, speed, 20, 1F, color, GlowTexture.STAR);
			}
		}
	}

	private void applyGravity() {
		world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(getPos().up()).grow(5)).forEach(e -> {
			double x = getPos().getX() + 0.5D - e.posX;
			double y = getPos().getY() + 0.5D - e.posY;
			double z = getPos().getZ() + 0.5D - e.posZ;
			double sqrt = Math.sqrt(x * x + y * y + z * z);
			double effect = sqrt / 5D;
			double strength = (1 - effect) * (1 - effect);
			double power = 0.005D;
			e.motionX += (x / sqrt) * strength * power;
			e.motionY += (y / sqrt) * strength * power;
			e.motionZ += (z / sqrt) * strength * power;
		});
	}

	public boolean canFill() {
		return handler.canFill() && handler.getFluidAmount() < handler.getCapacity();
	}

	public EnumFacing getFacingLazy() {
		return getStateValue(BlockDirectional.FACING, pos).orElse(EnumFacing.DOWN);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		EnumFacing f = getFacingLazy();
		return (f == facing || f.getOpposite() == facing) && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
				|| super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		EnumFacing f = getFacingLazy();
		return (f == facing || f.getOpposite() == facing) && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
				? CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(handler)
				: super.getCapability(capability, facing);
	}

	@Override
	void readNBT(NBTTagCompound compound) {
		handler.readFromNBT(compound);
	}

	@Override
	void writeNBT(NBTTagCompound compound) {
		handler.writeToNBT(compound);
	}
}
