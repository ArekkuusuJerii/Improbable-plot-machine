/*******************************************************************************
 * Arekkuusu / Solar 2017
 *
 * This project is licensed under the MIT.
 * The source code is available on github:
 * https://github.com/ArekkuusuJerii/Solar#solar
 ******************************************************************************/
package arekkuusu.solar.client.util.resource.shader;

import arekkuusu.solar.client.util.resource.ShaderManager;
import arekkuusu.solar.common.lib.LibMod;
import net.katsstuff.mirror.client.helper.AssetLocation;
import net.katsstuff.mirror.client.helper.ResourceHelperStatic;
import net.katsstuff.mirror.client.helper.ShaderLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBShaderObjects;

import javax.annotation.Nullable;

/**
 * Created by <Snack> on 13/01/2018.
 * It's distributed as part of Solar.
 */
public class ShaderResource {

	private ResourceLocation vsh;
	private ResourceLocation fsh;
	private int program;

	public ShaderResource(@Nullable String vsh, @Nullable String fsh) {
		if(vsh != null)
			this.vsh = ResourceHelperStatic.getLocation(LibMod.MOD_ID, AssetLocation.Shaders(), ShaderLocation.Program(), vsh, ".vsh");
		if(fsh != null)
			this.fsh = ResourceHelperStatic.getLocation(LibMod.MOD_ID, AssetLocation.Shaders(), ShaderLocation.Program(), fsh, ".fsh");
		this.reload(Minecraft.getMinecraft().getResourceManager());
	}

	public boolean begin() {
		if(program != 0 && !ShaderManager.isReloading()) {
			OpenGlHelper.glUseProgram(program);
			return true;
		}
		return false;
	}

	public void setF(String name, float val) {
		if(!ShaderManager.isReloading()) {
			int in = OpenGlHelper.glGetUniformLocation(program, name);
			ARBShaderObjects.glUniform1fARB(in, val);
		}
	}

	public void setI(String name, int val) {
		if(!ShaderManager.isReloading()) {
			int in = OpenGlHelper.glGetUniformLocation(program, name);
			ARBShaderObjects.glUniform1iARB(in, val);
		}
	}

	public void end() {
		OpenGlHelper.glUseProgram(0);
	}

	public void reload(IResourceManager manager) {
		this.program = ShaderManager.create(manager, vsh, fsh);
	}
}
