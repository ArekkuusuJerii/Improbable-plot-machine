/*******************************************************************************
 * Arekkuusu / Solar 2017
 *
 * This project is licensed under the MIT.
 * The source code is available on github:
 * https://github.com/ArekkuusuJerii/Solar#solar
 ******************************************************************************/
package arekkuusu.solar.common.block;

import arekkuusu.solar.common.block.tile.*;
import arekkuusu.solar.common.lib.LibMod;
import arekkuusu.solar.common.lib.LibNames;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Created by <Arekkuusu> on 21/06/2017.
 * It's distributed as part of Solar.
 */
@ObjectHolder(LibMod.MOD_ID)
public final class ModBlocks {

	private static final Block PLACE_HOLDER = new Block(Material.AIR);
	//--------------------------------Blocks--------------------------------//
	public static final Block PRIMAL_STONE = PLACE_HOLDER;
	public static final Block PRIMAL_GLYPH = PLACE_HOLDER;
	public static final Block singularity = PLACE_HOLDER;
	public static final Block PRISM_FLOWER = PLACE_HOLDER;
	public static final Block QUANTUM_MIRROR = PLACE_HOLDER;
	public static final Block GRAVITY_HOPPER = PLACE_HOLDER;
	public static final Block SCHRODINGER_GLYPH = PLACE_HOLDER;
	public static final Block CRYSTAL_VOID = PLACE_HOLDER;
	public static final Block BLINKER = PLACE_HOLDER;
	public static final Block PHENOMENA = PLACE_HOLDER;
	public static final Block Q_SQUARED = PLACE_HOLDER;
	public static final Block THEOREMA = PLACE_HOLDER;
	public static final Block GRAVITY_INHIBITOR = PLACE_HOLDER;
	public static final Block HYPER_CONDUCTOR = PLACE_HOLDER;
	public static final Block ELECTRON_NODE = PLACE_HOLDER;
	public static final Block ASHEN = PLACE_HOLDER;

	public static void register(IForgeRegistry<Block> registry) {
		registry.register(new BlockBase(LibNames.PRIMAL_STONE, Material.ROCK).setHardness(4F).setResistance(2000F));
		registry.register(new BlockPrimalGlyph());
		registry.register(new BlockSingularity());
		registry.register(new BlockPrismFlower());
		registry.register(new BlockQuantumMirror());
		registry.register(new BlockGravityHopper());
		registry.register(new BlockSchrodingerGlyph());
		registry.register(new BlockCrystalVoid());
		registry.register(new BlockBlinker());
		registry.register(new BlockPhenomena());
		registry.register(new BlockQSquared());
		registry.register(new BlockTheorema());
		registry.register(new BlockGravityInhibitor());
		registry.register(new BlockHyperConductor());
		registry.register(new BlockElectronNode());
		registry.register(new BlockAshen());
		registerTiles();
	}

	private static void registerTiles() {
		GameRegistry.registerTileEntity(TileSingularity.class, LibMod.MOD_ID + ":singularity");
		GameRegistry.registerTileEntity(TilePrismFlower.class, LibMod.MOD_ID + ":prism_flower");
		GameRegistry.registerTileEntity(TileQuantumMirror.class, LibMod.MOD_ID + ":quantum_mirror");
		GameRegistry.registerTileEntity(TileGravityHopper.class, LibMod.MOD_ID + ":gravity_hopper");
		GameRegistry.registerTileEntity(TileCrystalVoid.class, LibMod.MOD_ID + ":crystal_void");
		GameRegistry.registerTileEntity(TileBlinker.class, LibMod.MOD_ID + ":blinker");
		GameRegistry.registerTileEntity(TilePhenomena.class, LibMod.MOD_ID + ":phenomena");
		GameRegistry.registerTileEntity(TileQSquared.class, LibMod.MOD_ID + ":q_squared");
		GameRegistry.registerTileEntity(TileTheorema.class, LibMod.MOD_ID + ":theorema");
		GameRegistry.registerTileEntity(TileGravityInhibitor.class, LibMod.MOD_ID + ":gravity_inhibitor");
		GameRegistry.registerTileEntity(TileHyperConductor.class, LibMod.MOD_ID + ":hyper_conductor");
		GameRegistry.registerTileEntity(TileElectronNode.class, LibMod.MOD_ID + ":electron_node");

		GameRegistry.registerTileEntity(RenderDummy.Quingentilliard.class,LibMod.MOD_ID + ":quingentilliard_dummy");
	}
}
