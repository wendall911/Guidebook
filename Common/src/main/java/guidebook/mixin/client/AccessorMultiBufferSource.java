package guidebook.mixin.client;

import java.util.SequencedMap;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiBufferSource.BufferSource.class)
public interface AccessorMultiBufferSource {

	@Accessor("sharedBuffer")
	ByteBufferBuilder getFallbackBuffer();

	@Accessor("fixedBuffers")
	SequencedMap<RenderType, ByteBufferBuilder> getFixedBuffers();

}
