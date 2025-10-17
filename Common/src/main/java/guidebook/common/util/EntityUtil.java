package guidebook.common.util;

import java.util.Optional;
import java.util.function.Function;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import org.apache.commons.lang3.tuple.Pair;

import guidebook.api.GuidebookAPI;

public final class EntityUtil {

    private EntityUtil() {}

    public static String getEntityName(String entityId) {
        Pair<String, String> nameAndNbt = splitNameAndNBT(entityId);
        ResourceLocation id = ResourceLocation.tryParse(nameAndNbt.getLeft());
        String unknown = "Unknown Entity";

        if (id == null) {
            return unknown;
        }

        Optional<Reference<EntityType<?>>> type = BuiltInRegistries.ENTITY_TYPE.get(id);

        return type.map(entityTypeReference -> entityTypeReference.value().getDescriptionId()).orElse(unknown);

    }

    public static Function<Level, Entity> loadEntity(String entityId) {
        Pair<String, String> nameAndNbt = splitNameAndNBT(entityId);
        entityId = nameAndNbt.getLeft();
        String nbtStr = nameAndNbt.getRight();
        CompoundTag nbt = null;

        if (!nbtStr.isEmpty()) {
            try {
                nbt = TagParser.parseCompoundFully(nbtStr);
            }
            catch (CommandSyntaxException e) {
                GuidebookAPI.LOGGER.error("Failed to load entity data", e);
            }
        }

        ResourceLocation key = ResourceLocation.tryParse(entityId);
        Optional<EntityType<?>> maybeType = BuiltInRegistries.ENTITY_TYPE.getOptional(key);
        if (maybeType.isEmpty()) {
            throw new RuntimeException("Unknown entity id: " + entityId);
        }
        EntityType<?> entityType = maybeType.get();
        final CompoundTag useNbt = nbt;
        final String useId = entityId;

        return (level) -> {
            Entity entity;
            try {
                if (useNbt != null) {
                    entity = EntityType.loadEntityRecursive(useNbt, level, EntitySpawnReason.LOAD, displayOnly -> displayOnly);
                }
                else {
                    entity = entityType.create(level, EntitySpawnReason.LOAD);
                }

                return entity;
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Can't load entity " + useId, e);
            }
        };
    }

    private static Pair<String, String> splitNameAndNBT(String entityId) {
        int nbtStart = entityId.indexOf("{");
        String nbtStr = "";

        if (nbtStart > 0) {
            nbtStr = entityId.substring(nbtStart).replaceAll("([^\\\\])'", "$1\"").replaceAll("\\\\'", "'");
            entityId = entityId.substring(0, nbtStart);
        }

        return Pair.of(entityId, nbtStr);
    }

}
