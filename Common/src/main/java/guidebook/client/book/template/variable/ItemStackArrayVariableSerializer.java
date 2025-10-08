package guidebook.client.book.template.variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

import guidebook.common.util.ItemStackUtil;

public class ItemStackArrayVariableSerializer extends GenericArrayVariableSerializer<ItemStack> {

	public ItemStackArrayVariableSerializer() {
		super(new ItemStackVariableSerializer(), ItemStack.class);
	}

	@Override
	public ItemStack[] fromJson(JsonElement json, HolderLookup.Provider registries) {
		if (json.isJsonArray()) {
			JsonArray array = json.getAsJsonArray();
			List<ItemStack> stacks = new ArrayList<>();
			for (JsonElement e : array) {
				stacks.addAll(Arrays.asList(fromNonArray(e, registries)));
			}
			return stacks.toArray(empty);
		}
		return fromNonArray(json, registries);
	}

	public ItemStack[] fromNonArray(JsonElement json, HolderLookup.Provider registries) {
		if (json.isJsonNull()) {
			return empty;
		}
		if (json.isJsonPrimitive()) {
			return ItemStackUtil.loadStackListFromString(json.getAsString(), registries).toArray(empty);
		}
		if (json.isJsonObject()) {
			return new ItemStack[] { ItemStackUtil.loadStackFromJson(json.getAsJsonObject(), registries) };
		}
		throw new IllegalArgumentException("Can't make an ItemStack from an array!");
	}

}
