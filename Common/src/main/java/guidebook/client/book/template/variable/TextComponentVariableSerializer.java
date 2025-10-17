package guidebook.client.book.template.variable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import com.mojang.serialization.JsonOps;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

import guidebook.api.IVariableSerializer;
import guidebook.common.util.SerializationUtil;

public class TextComponentVariableSerializer implements IVariableSerializer<Component> {

    @Override
    public Component fromJson(JsonElement json, HolderLookup.Provider provider) {
        if (json.isJsonNull()) {
            return Component.literal("");
        }
        if (json.isJsonPrimitive()) {
            return Component.literal(json.getAsString());
        }

        return SerializationUtil.ComponentSerializer.fromJson(json, provider);
    }

    @Override
    public JsonElement toJson(Component stack, HolderLookup.Provider provider) {
        return ComponentSerialization.CODEC.encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), stack)
            .getOrThrow(JsonParseException::new);
    }

}
