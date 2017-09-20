/*******************************************************************************
 * Arekkuusu / Solar 2017
 *
 * This project is licensed under the MIT.
 * The source code is available on github:
 * https://github.com/ArekkuusuJerii/Solar#solar
 ******************************************************************************/
package arekkuusu.solar.common.network;

import arekkuusu.solar.api.helper.Vector3;
import arekkuusu.solar.client.effect.ParticleUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

/**
 * Created by <Arekkuusu> on 17/09/2017.
 * It's distributed as part of Solar.
 */
public class QTeleportEffectMessage implements IMessage {

	private Vector3 from;
	private Vector3 to;

	public QTeleportEffectMessage() {}

	public QTeleportEffectMessage(Vector3 from, Vector3 to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer beef = new PacketBuffer(buf);
		from = readVec(beef);
		to = readVec(beef);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer beef = new PacketBuffer(buf);
		writeVec(from, beef);
		writeVec(to, beef);
	}

	private void writeVec(Vector3 vec, PacketBuffer beef) {
		beef.writeDouble(vec.x);
		beef.writeDouble(vec.y);
		beef.writeDouble(vec.z);
	}

	private Vector3 readVec(PacketBuffer beef) {
		return new Vector3(beef.readDouble(), beef.readDouble(), beef.readDouble());
	}

	public static class QTeleportEffectMessageHandler implements IMessageHandler<QTeleportEffectMessage, IMessage> {

		@Override
		@Nullable
		@SuppressWarnings("MethodCallSideOnly")
		public IMessage onMessage(QTeleportEffectMessage message, MessageContext ctx) {
			if(ctx.side == Side.CLIENT) {
				Minecraft.getMinecraft().addScheduledTask(() -> {
					Vector3 speed = message.from.copy().subtract(message.to).multiply(0.2D);
					World world = Minecraft.getMinecraft().player.world;

					for(int i = 0; i < 15; i++) {
						ParticleUtil.spawnLightParticle(world, message.from.x, message.from.y, message.from.z
								, speed.x, speed.y, speed.z, 0xFF0303, 60, 0.25F + (world.rand.nextFloat() * 0.5F));
					}
				});
			}
			return null;
		}
	}
}
