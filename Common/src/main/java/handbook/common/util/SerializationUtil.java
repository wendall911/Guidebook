package handbook.common.util;

import java.lang.reflect.Type;
import java.util.Locale;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import com.mojang.serialization.JsonOps;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

import handbook.api.IVariable;

public final class SerializationUtil {

    public static final IVariable.Serializer VARIABLE_SERIALIZER = new IVariable.Serializer();
    public static final Gson RAW_GSON = new GsonBuilder()
        .registerTypeAdapter(Identifier.class, new IdentifierSerializer())
        .registerTypeAdapter(IVariable.class, VARIABLE_SERIALIZER)
        .create();
    public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    private SerializationUtil() {}

    public static Identifier getAsIdentifier(JsonObject object, String key, @Nullable Identifier fallback) {
        if (object.has(key)) {
            return Identifier.tryParse(GsonHelper.convertToString(object.get(key), key));
        }
        else {
            return fallback;
        }
    }

    @Nullable
    public static <T extends Enum<T>> T getAsEnum(JsonObject object, String key, Class<T> clz, @Nullable T fallback) {
        if (object.has(key)) {
            var str = GsonHelper.convertToString(object.get(key), key).toUpperCase(Locale.ROOT);
            return Enum.valueOf(clz, str);
        }
        else {
            return fallback;
        }
    }

    public static class IdentifierSerializer implements JsonDeserializer<Identifier>, JsonSerializer<Identifier> {

        @Override
        public Identifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            return Identifier.parse(GsonHelper.convertToString(jsonElement, "location"));
        }

        @Override
        public JsonElement serialize(Identifier location, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(location.toString());
        }

    }

    public static class ComponentSerializer {

        public static MutableComponent fromJson(JsonElement json, HolderLookup.Provider provider) {
            return json == null ? null : deserialize(json, provider);
        }

        private static MutableComponent deserialize(JsonElement json, Provider provider) {
            return (MutableComponent) ComponentSerialization.CODEC.parse(
                provider.createSerializationContext(JsonOps.INSTANCE),
                json
            ).getOrThrow(JsonParseException::new);
        }

    }

}
