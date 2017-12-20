/*******************************************************************************
 * Arekkuusu / Solar 2017
 *
 * This project is licensed under the MIT.
 * The source code is available on github:
 * https://github.com/ArekkuusuJerii/Solar#solar
 ******************************************************************************/
package arekkuusu.solar.common.network;

import arekkuusu.solar.api.entanglement.quantum.QuantumHandler;
import arekkuusu.solar.api.helper.Vector3;
import arekkuusu.solar.client.effect.ParticleUtil;
import arekkuusu.solar.common.block.tile.TilePhenomena;
import arekkuusu.solar.common.lib.LibMod;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by <Arekkuusu> on 21/06/2017.
 * It's distributed as part of Solar.
 */
public class PacketHandler {

	public static final List<IPacketHandler> HANDLERS = Lists.newArrayList();

	public static final IPacketHandler Q_SYNC_ALL = (compound, context) -> {
		Map<UUID, List<ItemStack>> map = Maps.newHashMap();
		NBTTagList tags = (NBTTagList) compound.getTag("tags");
		for(NBTBase nbt : tags) {
			NBTTagCompound tag = (NBTTagCompound) nbt;
			NBTTagList list = (NBTTagList) tag.getTag("list");
			UUID uuid = tag.getUniqueId("uuid");
			List<ItemStack> stacks = Lists.newArrayList();
			for(NBTBase stack : list) {
				stacks.add(new ItemStack((NBTTagCompound) stack));
			}
			map.put(uuid, stacks);
		}
		QuantumHandler.getEntanglements().putAll(map);
	};

	public static final IPacketHandler Q_SYNC_SOME = (compound, context) -> {
		NBTTagList list = (NBTTagList) compound.getTag("list");
		UUID uuid = compound.getUniqueId("uuid");
		List<ItemStack> stacks = Lists.newArrayList();
		for(NBTBase stack : list) {
			stacks.add(new ItemStack((NBTTagCompound) stack));
		}

		QuantumHandler.getEntanglements().replace(uuid, stacks);
	};

	public static final IPacketHandler Q_SYNC_CHANGE = (compound, context) -> {
		ItemStack stack = new ItemStack((NBTTagCompound) compound.getTag("stack"));
		UUID uuid = compound.getUniqueId("uuid");
		int slot = compound.getInteger("slot");
		QuantumHandler.setEntanglementStack(uuid, stack, slot);
	};

	public static final IPacketHandler PHENOMENA = (compound, context) -> {
		BlockPos pos = new BlockPos(compound.getInteger("x"), compound.getInteger("y"), compound.getInteger("z"));
		TileEntity tile = Minecraft.getMinecraft().player.world.getTileEntity(pos);
		if(tile instanceof TilePhenomena) {
			((TilePhenomena) tile).makePhenomenon();
		}
	};

	public static final IPacketHandler QUARTZ_EFFECT = new IPacketHandler() {
		@Override
		@SideOnly(Side.CLIENT)
		public void handleData(NBTTagCompound compound, MessageContext context) {
			World world = Minecraft.getMinecraft().player.world;
			Vector3 from = new Vector3(compound.getCompoundTag("from"));
			Vector3 to = new Vector3(compound.getCompoundTag("to"));
			for(int i = 0; i < 15; i++) {
				Vector3 offset = Vector3.getRandomVec(0.1D).add(from);
				Vector3 speed = Vector3.getRandomVec(0.1D);
				ParticleUtil.spawnTunnelingPhoton(world, offset,
						speed, 0xFF0303, 60, 0.35F + (world.rand.nextFloat() * 0.5F));
			}
			for(int i = 0; i < 15; i++) {
				Vector3 offset = Vector3.getRandomVec(0.05D).add(to);
				Vector3 speed = Vector3.getRandomVec(0.01D);
				ParticleUtil.spawnTunnelingPhoton(world, offset,
						speed, 0x49FFFF, 60, 0.35F + (world.rand.nextFloat() * 0.5F));
			}
		}
	};

	public static final SimpleNetworkWrapper NETWORK = new SimpleNetworkWrapper(LibMod.MOD_ID);
	private static int id = 0;

	public static void init() {
		register(ServerToClientPacket.Handler.class, ServerToClientPacket.class, Side.CLIENT);
		register(ClientToServerPacket.Handler.class, ClientToServerPacket.class, Side.SERVER);

		HANDLERS.add(Q_SYNC_ALL);
		HANDLERS.add(Q_SYNC_SOME);
		HANDLERS.add(Q_SYNC_CHANGE);
		HANDLERS.add(PHENOMENA);
		HANDLERS.add(QUARTZ_EFFECT);
	}

	private static <H extends IMessageHandler<M, IMessage>, M extends IMessage> void register(Class<H> handler, Class<M> message, Side side) {
		NETWORK.registerMessage(handler, message, id++, side);
	}
}
