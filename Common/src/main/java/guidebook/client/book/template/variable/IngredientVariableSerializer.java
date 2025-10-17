package guidebook.client.book.template.variable;

import com.google.gson.JsonElement;

import com.mojang.serialization.JsonOps;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.Ingredient;

import guidebook.api.IVariableSerializer;
import guidebook.common.util.ItemStackUtil;

public class IngredientVariableSerializer implements IVariableSerializer<Ingredient> {

	@Override
	public Ingredient fromJson(JsonElement json, HolderLookup.Provider registries) {
		return (json.isJsonPrimitive())
            ? ItemStackUtil.loadIngredientFromString(json.getAsString(), registries)
            : ItemStackUtil.loadTagFromJson(json.getAsJsonObject(), registries);
	}

	@Override
	public JsonElement toJson(Ingredient stack, HolderLookup.Provider registries) {
        return Ingredient.CODEC.encodeStart(
            registries.createSerializationContext(JsonOps.INSTANCE), stack).result().orElseThrow();
	}

}
