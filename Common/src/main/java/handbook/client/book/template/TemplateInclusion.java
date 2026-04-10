package handbook.client.book.template;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;

import handbook.api.IComponentProcessor;
import handbook.api.IVariable;
import handbook.api.IVariableProvider;

public class TemplateInclusion {

    /**
     * The template to include.
     */
    public String template;

    /**
     * The scope under which the included template's variables are exposed to the including template.
     * Modified on load to become absolute, i.e. the full path from the top level page to the
     * included template's variables.
     */
    public String as;

    /**
     * Bindings to perform on the included template.
     * Right hand side can reference variables in the including template.
     */
    @SerializedName("using") public JsonObject localBindings = new JsonObject();

    public int x, y;

    transient Set<String> visitedTemplates = new LinkedHashSet<>();

    public void upperMerge(@Nullable TemplateInclusion parent) {
        if (parent == null) {
            return;
        }

        if (parent.visitedTemplates.contains(template)) {
            throw new IllegalArgumentException(
                "Breaking when include template " +
                template + ", circular dependencies aren't allowed (stack = " +
                parent.visitedTemplates + ")"
            );
        }

        visitedTemplates = new LinkedHashSet<>(parent.visitedTemplates);
        visitedTemplates.add(template);
        as = parent.qualifyName(as);
        x += parent.x;
        y += parent.y;

        for (Map.Entry<String, JsonElement> entry : localBindings.entrySet()) {
            JsonElement value = entry.getValue();

            if (value.isJsonPrimitive() && value.getAsString().startsWith("#")) {
                String realVal = value.getAsString().substring(1);

                if (parent.localBindings.has(realVal)) {
                    entry.setValue(parent.localBindings.get(realVal));
                }
            }
        }
    }

    public void process(Level level, IComponentProcessor processor) {
        if (processor == null) {
            return;
        }

        for (Map.Entry<String, JsonElement> entry : localBindings.entrySet()) {
            JsonElement value = entry.getValue();

            if (value.isJsonPrimitive() && value.getAsString().startsWith("#")) {
                String realVal = value.getAsString().substring(1);
                IVariable result = processor.process(level, realVal);

                if (result != null) {
                    entry.setValue(result.unwrap());
                }
            }
        }
    }

    public String qualifyName(String name) {
        boolean prefixed = name.startsWith("#");
        String query = prefixed ? name.substring(1) : name;

        // if it's an upreference, return the upreference
        String result = IVariable.wrap(localBindings.get(query),
            RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)).asString();

        if (result.startsWith("#")) {
            return result.substring(1);
        }

        return (prefixed ? "#" : "") + as + (query.isEmpty() ? "" : "." + query);
    }

    /**
     * Attempt to look up a variable in local scope.
     */
    public IVariable attemptVariableLookup(String key, HolderLookup.Provider registries) {
        if (key.startsWith("#")) {
            key = key.substring(1);
        }

        IVariable result = IVariable.wrap(localBindings.get(key), registries);

        return result.asString().isEmpty() || isUpreference(result) ? null : result;
    }

    /**
     * Check if this variable is actually a string starting with "#".
     */
    public boolean isUpreference(IVariable v) {
        return v.unwrap().isJsonPrimitive() && v.asString().startsWith("#");
    }

    public IVariableProvider wrapProvider(IVariableProvider provider) {
        return new IVariableProvider() {
            @Override
            public boolean has(String key) {
                return attemptVariableLookup(key, RegistryAccess.EMPTY) != null || provider.has(qualifyName(key));
            }

            @Override
            public IVariable get(String key, HolderLookup.Provider registries) {
                IVariable iVariable = attemptVariableLookup(key, registries);

                return iVariable == null ? provider.get(qualifyName(key), registries) : iVariable;
            }
        };
    }

}
