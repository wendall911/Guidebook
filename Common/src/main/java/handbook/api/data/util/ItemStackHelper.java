package handbook.api.data.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.world.item.ItemStackTemplate;

public class ItemStackHelper {

	private static final Gson GSON = new GsonBuilder().create();

	public static String serializeStack(ItemStackTemplate stack) {
		return new ItemInput(stack.item(), stack.components()).item().getRegisteredName() + (stack.count() == 1 ? "" :("#" + stack.count()));
	}

}
