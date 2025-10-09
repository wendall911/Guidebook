package guidebook.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.resources.ResourceLocation;

import org.apache.commons.lang3.tuple.Pair;

import guidebook.api.GuidebookAPI;
import guidebook.common.CommonModContainer;
import guidebook.platform.Services;

import technology.roughness.whitenoise.config.WhiteNoiseConfigSpec;

public class GuidebookConfig {

    public static final WhiteNoiseConfigSpec CLIENT_SPEC;
    private static final Client CLIENT;
    private static final Map<String, Boolean> CONFIG_FLAGS = new ConcurrentHashMap<>();

    static {
        Pair<Client, WhiteNoiseConfigSpec> specPair = new WhiteNoiseConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public static final class Client {
        private final WhiteNoiseConfigSpec.ConfigValue<Boolean> disableAdvancementLocking;
        private final WhiteNoiseConfigSpec.ConfigValue<List<? extends String>> noAdvancementBooks;
        private final WhiteNoiseConfigSpec.ConfigValue<Boolean> testingMode;
        private final WhiteNoiseConfigSpec.ConfigValue<String> inventoryButtonBook;
        private final WhiteNoiseConfigSpec.ConfigValue<Boolean> useShiftForQuickLookup;
        private final WhiteNoiseConfigSpec.EnumValue<TextOverflowMode> overflowMode;
        private final WhiteNoiseConfigSpec.ConfigValue<Integer> quickLookupTime;

        Client(WhiteNoiseConfigSpec.Builder builder) {
            disableAdvancementLocking = builder
                .comment(
                    "Set this to true to disable advancement locking for ALL books, ",
                    "making all entries visible at all times. Config Flag: advancements_disabled"
                )
                .define("disableAdvancementLocking", false);

            noAdvancementBooks = builder
                .comment(
                    "Granular list of Book ID's to disable advancement locking for, ",
                    "e.g. [ \"botania:lexicon\" ]. Config Flags: advancements_disabled_<bookid>"
                )
                .defineListAllowEmpty(List.of("noAdvancementBooks"), Collections::emptyList,
                    o -> o instanceof String s && ResourceLocation.tryParse(s) != null);

            testingMode = builder
                .comment(
                    "Enable testing mode. By default this doesn't do anything, but you can use the config ",
                    "flag in your books if you want. Config Flag: testing_mode"
                )
                .define("testingMode", false);

            inventoryButtonBook = builder
                .comment("Set this to the ID of a book to have it show up in players' inventories, replacing the recipe book.")
                .define("inventoryButtonBook", "");

            useShiftForQuickLookup = builder
                .comment("Set this to true to use Shift instead of Ctrl for the inventory quick lookup feature.")
                .define("useShiftForQuickLookup", false);

            overflowMode = builder
                .comment(
                    "Set how text overflow should be coped with: overflow the text off the page, ",
                    "truncate overflowed text, or resize everything to fit. Relogin after changing."
                )
                .defineEnum("textOverflowMode", TextOverflowMode.RESIZE);

            quickLookupTime = builder
                .comment("How long in ticks the quick lookup key needs to be pressed before the book opens")
                .define("quickLookupTime", 10);
        }

        public static boolean disableAdvancementLocking() {
            return CLIENT.disableAdvancementLocking.get();
        }

        public static List<? extends String> noAdvancementBooks() {
            return CLIENT.noAdvancementBooks.get();
        }

        public static boolean testingMode() {
            return CLIENT.testingMode.get();
        }

        public static String inventoryButtonBook() {
            return CLIENT.inventoryButtonBook.get();
        }

        public static boolean useShiftForQuickLookup() {
            return CLIENT.useShiftForQuickLookup.get();
        }

        public static TextOverflowMode overflowMode() {
            return CLIENT.overflowMode.get();
        }

        public static int quickLookupTime() {
            return CLIENT.quickLookupTime.get();
        }

    }

    public enum TextOverflowMode {
        OVERFLOW,
        TRUNCATE,
        RESIZE
    }
    public static void reloadBuiltinFlags() {
        Collection<CommonModContainer> mods = Services.BOOK_HELPER.getAllMods();
        for (CommonModContainer info : mods) {
            setFlag("mod:" + info.getId(), true);
        }

        setFlag("debug", Services.BOOK_HELPER.isDevEnvironment());
        setFlag("advancements_disabled", Client.disableAdvancementLocking());
        setFlag("testing_mode", Client.testingMode());

        for (String book : Client.noAdvancementBooks()) {
            setFlag("advancements_disabled_" + book, true);
        }
    }

    public static boolean getConfigFlag(String name) {
        if (name.startsWith("&")) {
            return getConfigFlagAND(name.replaceAll("[&|]", "").split(","));
        }
        else if (name.startsWith("|")) {
            return getConfigFlagOR(name.replaceAll("[&|]", "").split(","));
        }

        boolean target = true;

        if (name.startsWith("!")) {
            name = name.substring(1);
            target = false;
        }
        name = name.trim().toLowerCase(Locale.ROOT);

        Boolean b = CONFIG_FLAGS.get(name);

        if (b == null) {
            if (!name.startsWith("mod:")) {
                GuidebookAPI.LOGGER.warn("Queried for unknown config flag: {}", name);
            }
            b = false;
        }

        return b == target;
    }

    public static boolean getConfigFlagAND(String[] tokens) {
        for (String s : tokens) {
            if (!getConfigFlag(s)) {
                return false;
            }
        }

        return true;
    }

    public static boolean getConfigFlagOR(String[] tokens) {
        for (String s : tokens) {
            if (getConfigFlag(s)) {
                return true;
            }
        }

        return false;
    }

    public static void setFlag(String flag, boolean value) {
        CONFIG_FLAGS.put(flag.trim().toLowerCase(Locale.ROOT), value);
    }

}
