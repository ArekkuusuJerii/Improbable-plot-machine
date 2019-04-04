/*
 * Arekkuusu / Improbable plot machine. 2018
 *
 * This project is licensed under the MIT.
 * The source code is available on github:
 * https://github.com/ArekkuusuJerii/Improbable-plot-machine
 */
package arekkuusu.implom.common.item;

import arekkuusu.implom.api.capability.INBTDataTransferable;
import arekkuusu.implom.api.capability.PositionsHelper;
import arekkuusu.implom.common.block.ModBlocks;
import arekkuusu.implom.common.handler.data.capability.nbt.PositionsNBTDataCapability;
import arekkuusu.implom.common.handler.data.capability.provider.PositionsNBTProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by <Arekkuusu> on 17/01/2018.
 * It's distributed as part of Improbable plot machine.
 */
public class ItemMechanicalTranslocator extends ItemBaseBlock implements IUUIDDescription {

	public ItemMechanicalTranslocator() {
		super(ModBlocks.MECHANICAL_TRANSLOCATOR);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		PositionsHelper.getCapability(stack).ifPresent(instance -> {
			if(instance.getKey() != null) addInformation(instance.getKey(), tooltip, INBTDataTransferable.DefaultGroup.TRANSLOCATOR);
		});
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new PositionsNBTProvider(new PositionsNBTDataCapability());
	}
}
