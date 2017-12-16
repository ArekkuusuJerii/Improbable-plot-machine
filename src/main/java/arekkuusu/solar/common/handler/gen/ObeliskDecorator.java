/*******************************************************************************
 * Arekkuusu / Solar 2017
 *
 * This project is licensed under the MIT.
 * The source code is available on github:
 * https://github.com/ArekkuusuJerii/Solar#solar
 ******************************************************************************/
package arekkuusu.solar.common.handler.gen;

import arekkuusu.solar.api.helper.RandomCollection;
import arekkuusu.solar.api.helper.Vector3;
import com.google.common.collect.Lists;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

import java.util.List;

import static arekkuusu.solar.common.handler.ConfigHandler.GEN_CONFIG;

/**
 * Created by <Arekkuusu> on 09/12/2017.
 * It's distributed as part of Solar.
 */
public class ObeliskDecorator extends BaseGen {

	private final RandomCollection<ModGen.Structure> obelisks = new RandomCollection<ModGen.Structure>(random)
			.add(GEN_CONFIG.MONOLITH_CONFIG.OBELISK_DECORATOR.WEIGHTS.monolithic, ModGen.Structure.MONOLITH_OBELISK)
			.add(GEN_CONFIG.MONOLITH_CONFIG.OBELISK_DECORATOR.WEIGHTS.fragmented, ModGen.Structure.MONOLITH_OBELISK_FRAGMENTED);

	@Override
	void gen(World world, int x, int z, IChunkGenerator generator, IChunkProvider provider) {
		random.setSeed(world.getSeed());
		long good = random.nextLong();
		long succ = random.nextLong();

		good *= x >> 3;
		succ *= z >> 3;
		random.setSeed(good * succ * world.getSeed());
		//Generate
		if(GEN_CONFIG.MONOLITH_CONFIG.OBELISK_DECORATOR.rarity > 0D
				&& GEN_CONFIG.MONOLITH_CONFIG.OBELISK_DECORATOR.rarity / 100D > random.nextDouble()) {
			List<AxisAlignedBB> occupied = Lists.newArrayList();
			for(int i = 0; i < GEN_CONFIG.MONOLITH_CONFIG.OBELISK_DECORATOR.size; i++) {
				BlockPos top = world.getTopSolidOrLiquidBlock(randomVector().add(x, 0, z).toBlockPos());
				int below = random.nextInt(7);
				if(top.getY() > below) {
					top = top.add(0, -below, 0);
				}
				Template obelisk = obelisks.next().load(world);
				Rotation rotation = Rotation.values()[random.nextInt(4)];
				Vector3 vec = new Vector3(obelisk.getSize()).rotate(rotation);
				AxisAlignedBB obeliskBB = new AxisAlignedBB(top, vec.add(top).toBlockPos()).grow(1);
				if(occupied.stream().noneMatch(bb -> bb.intersects(obeliskBB))) {
					PlacementSettings settings = new PlacementSettings();
					settings.setRotation(rotation);
					settings.setRandom(random);
					obelisk.addBlocksToWorld(world, top, settings);

					occupied.add(obeliskBB);
				}
			}
		}
	}

	private Vector3 randomVector() {
		double x = 3 + random.nextInt(7);
		double z = 3 + random.nextInt(7);
		return new Vector3(x, 0, z);
	}
}