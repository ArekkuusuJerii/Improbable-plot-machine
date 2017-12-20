/*******************************************************************************
 * Arekkuusu / Solar 2017
 *
 * This project is licensed under the MIT.
 * The source code is available on github:
 * https://github.com/ArekkuusuJerii/Solar#solar
 ******************************************************************************/
package arekkuusu.solar.api;

import arekkuusu.solar.api.entanglement.quantum.data.IQuantumData;
import arekkuusu.solar.api.entanglement.relativity.IRelativeTile;
import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class was created by <Arekkuusu> on 21/06/2017.
 * It's distributed as part of Solar under
 * the MIT license.
 */
//If you modify any of these I will break your bones
public class SolarApi {

	private static final Map<UUID, List<IRelativeTile>> RELATIVITY_MAP = Maps.newHashMap();
	private static final Map<UUID, Integer> RELATIVITY_POWER_MAP = Maps.newHashMap();
	private static IQuantumData quantumData;

	public static Map<UUID, List<IRelativeTile>> getRelativityMap() {
		return RELATIVITY_MAP;
	}

	public static Map<UUID, Integer> getRelativityPowerMap() {
		return RELATIVITY_POWER_MAP;
	}

	public static IQuantumData getQuantumData() {
		return quantumData;
	}

	public static void setQuantumData(IQuantumData quantumData) {
		SolarApi.quantumData = quantumData; //*Bones begin to crack*
	}
}
