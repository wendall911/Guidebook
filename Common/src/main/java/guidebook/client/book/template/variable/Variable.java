package guidebook.client.book.template.variable;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;

import net.minecraft.core.HolderLookup;

import guidebook.api.IVariable;
import guidebook.api.IVariableSerializer;
import guidebook.api.VariableHelper;

public class Variable implements IVariable {

	private final JsonElement value;

	@Nullable private final Class<?> sourceClass;
	private final HolderLookup.Provider registries;

	public Variable(JsonElement elem, @Nullable Class<?> sourceClass, HolderLookup.Provider provider) {
		value = Objects.requireNonNull(elem);
		this.sourceClass = sourceClass;
		registries = provider;
	}

	@Override
	public <T> T as(Class<T> clazz) {
		IVariableSerializer<T> serializer = VariableHelper.instance().serializerForClass(clazz);

		if (serializer == null) {
			throw new IllegalArgumentException(String.format("Can't deserialize object of class %s from IVariable", clazz));
		}

		return serializer.fromJson(value, registries);
	}

	@Override
	public JsonElement unwrap() {
		return value;
	}

	@Override
	public String toString() {
		return value.toString();
	}

}
