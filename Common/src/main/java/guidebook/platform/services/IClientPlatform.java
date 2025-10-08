package guidebook.platform.services;

import java.util.ServiceLoader;
import java.util.stream.Collectors;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import guidebook.api.GuidebookAPI;

public interface IClientPlatform {

	// NB: Fluids handled at callsite in platform-independent manner
	void renderForMultiblock(BlockState state, BlockPos pos, BlockAndTintGetter multiblock, PoseStack ps, MultiBufferSource buffers, RandomSource rand);

	IClientPlatform INSTANCE = find();

	private static IClientPlatform find() {
		var providers = ServiceLoader.load(IClientPlatform.class, IClientPlatform.class.getClassLoader()).stream().toList();
		if (providers.size() != 1) {
			var names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
			throw new IllegalStateException("There should be exactly one IClientPlatform implementation on the classpath. Found: " + names);
		} else {
			var provider = providers.get(0);
			GuidebookAPI.LOGGER.debug("Instantiating client common impl: " + provider.type().getName());
			return provider.get();
		}
	}

}
