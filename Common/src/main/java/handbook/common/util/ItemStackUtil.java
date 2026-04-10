package handbook.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import com.google.gson.JsonObject;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;

import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import org.apache.commons.lang3.tuple.Triple;

import technology.roughness.whitenoise.util.ResourceLocationHelper;

import handbook.api.HandbookAPI;
import handbook.common.book.Book;
import handbook.common.book.BookRegistry;
import handbook.common.item.HandbookBook;

public final class ItemStackUtil {

    private ItemStackUtil() {}

    public static Triple<Holder<Item>, DataComponentPatch, Integer> deserializeStack(String string,
            HolderLookup.Provider registries) {
        StringReader reader = new StringReader(string.trim());
        ItemParser itemParser = new ItemParser(registries);
        try {
            ItemInput result = itemParser.parse(reader);
            int count = 1;
            if (reader.canRead()) {
                reader.expect('#');
                count = reader.readInt();
            }
            return Triple.of(result.item(), result.components(), count);
        }
        catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static ItemStack loadFromParsed(Triple<Holder<Item>, DataComponentPatch, Integer> parsed) {
        Holder<Item> holder = parsed.getLeft();
        DataComponentPatch components = parsed.getMiddle();
        Integer count = parsed.getRight();

        if (!holder.isBound() && holder.unwrapKey().isPresent()) {
            throw new RuntimeException("Unknown item ID: " + holder.unwrapKey().get().identifier());
        }

        Item item = holder.value();
        ItemStack stack = new ItemStack(item, count);

        if (!components.isEmpty()) {
            stack.applyComponents(components);
        }

        return stack;
    }

    public static ItemStack loadStackFromString(String res, HolderLookup.Provider registries) {
        return loadFromParsed(deserializeStack(res, registries));
    }

    public static Ingredient loadIngredientFromString(String ingredientString, HolderLookup.Provider registries) {
        List<Item> items = loadStackListFromString(ingredientString, registries).stream().map(ItemStack::getItem).toList();

        if (!items.isEmpty()) {
            return Ingredient.of(items.stream());
        }
        else {
            HandbookAPI.LOGGER.error("Empty ingredient not allowed. ID: {}", ingredientString);

            return null;
        }
    }

    public static List<ItemStack> loadStackListFromString(String ingredientString, HolderLookup.Provider registries) {
        String[] stacksSerialized = splitStacksFromSerializedIngredient(ingredientString);
        List<ItemStack> stacks = new ArrayList<>();

        for (String s : stacksSerialized) {
            if (s.isEmpty())
                continue;
            if (s.startsWith("tag:")) {
                Identifier location = Identifier.tryParse(s.substring(4));

                if (location == null) {
                    HandbookAPI.LOGGER.error("Invalid tag ID: " + s.substring(4));

                    continue;
                }

                TagKey<Item> key = TagKey.create(Registries.ITEM, location);

                registries.lookupOrThrow(Registries.ITEM).get(key).stream().flatMap(HolderSet::stream).forEach(
                    item -> stacks.add(new ItemStack(item))
                );
            }
            else {
                stacks.add(loadStackFromString(s, registries));
            }
        }

        return stacks;
    }

    public static StackWrapper wrapStack(ItemStack stack) {
        return stack.isEmpty() ? StackWrapper.EMPTY_WRAPPER : new StackWrapper(stack);
    }

    /**
     * Attempts to get a book instance from an item stack. This checks if the stack is a book item, and if not,
     * checks if the stack matches any registered book item.
     * Originally, this required having the data component on the stack, but this caused problems with
     * item comparisons in some cases (e.g. JEI ingredient matching), so now it checks against all registered books.
     *
     * @param stack the stack
     * @return the book, or null if none found
     */
    @Nullable
    public static Book getBookFromStack(ItemStack stack) {
        if (stack.getItem() instanceof HandbookBook) {
            return HandbookBook.getBook(stack);
        }

        Identifier stackId = ResourceLocationHelper.getItemStackId(stack);

        for (Map.Entry<Identifier, Book> entry : BookRegistry.INSTANCE.books.entrySet()) {
            if (ItemStack.isSameItem(entry.getValue().getBookItem(), stack)) {
                return entry.getValue();
            }
            else if (entry.getKey().equals(stackId)) {
                return entry.getValue();
            }
        }

        return null;
    }

    public static class StackWrapper {

        public static final StackWrapper EMPTY_WRAPPER = new StackWrapper(ItemStack.EMPTY);

        public final ItemStack stack;

        public StackWrapper(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this || (obj instanceof StackWrapper && ItemStack.isSameItem(stack, ((StackWrapper) obj).stack));
        }

        @Override
        public int hashCode() {
            return stack.getItem().hashCode();
        }

        @Override
        public String toString() {
            return "Wrapper[" + stack.toString() + "]";
        }

    }

    private static String[] splitStacksFromSerializedIngredient(String ingredientSerialized) {
        final List<String> result = new ArrayList<>();

        int lastIndex = 0;
        int braces = 0;
        int brackets = 0;
        Character insideString = null;
        for (int i = 0; i < ingredientSerialized.length(); i++) {
            switch (ingredientSerialized.charAt(i)) {
            case '{':
                if (insideString == null) {
                    braces++;
                }
                break;
            case '}':
                if (insideString == null) {
                    braces--;
                }
                break;
            case '[':
                if (insideString == null) {
                    brackets++;
                }
                break;
            case ']':
                if (insideString == null) {
                    brackets--;
                }
                break;
            case '\'':
                insideString = insideString == null ? '\'' : null;
                break;
            case '"':
                insideString = insideString == null ? '"' : null;
                break;
            case ',':
                if (braces <= 0 && brackets <= 0) {
                    result.add(ingredientSerialized.substring(lastIndex, i));
                    lastIndex = i + 1;
                    break;
                }
            }
        }

        result.add(ingredientSerialized.substring(lastIndex));

        return result.toArray(new String[0]);
    }

    public static ItemStack loadStackFromJson(JsonObject json, HolderLookup.Provider registries) {
        String itemName = json.get("item").getAsString();

        Item item = BuiltInRegistries.ITEM.getOptional(
            Identifier.tryParse(itemName)).orElseThrow(() -> new IllegalArgumentException("Unknown item '" + itemName + "'")
        );

        ItemStack stack = new ItemStack(item, GsonHelper.getAsInt(json, "count", 1));

        if (json.has("components")) {
            DataComponentMap.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE), json.get("components")).result()
                    .ifPresent(stack::applyComponents);
        }

        return stack;
    }

    public static Ingredient loadTagFromJson(JsonObject json, HolderLookup.Provider registries) {
        if (json.has("tag")) {
            return loadIngredientFromString(
                "tag:" + json.get("tag").getAsString(),
                registries
            );
        }

        return null;
    }

}
