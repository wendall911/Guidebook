package guidebook.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;

import technology.roughness.whitenoise.config.WhiteNoiseConfigSpec;

import guidebook.api.GuidebookAPI;
import guidebook.common.CommonModContainer;
import guidebook.common.Translations;
import guidebook.platform.Services;

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
        private final WhiteNoiseConfigSpec.BooleanValue disableAdvancementLocking;
        private final WhiteNoiseConfigSpec.ConfigValue<List<? extends String>> noAdvancementBooks;
        private final WhiteNoiseConfigSpec.BooleanValue testingMode;
        private final WhiteNoiseConfigSpec.ConfigValue<String> inventoryButtonBook;
        private final WhiteNoiseConfigSpec.BooleanValue useShiftForQuickLookup;
        private final WhiteNoiseConfigSpec.EnumValue<TextOverflowMode> overflowMode;
        private final WhiteNoiseConfigSpec.IntValue quickLookupTime;
        private static final Predicate<Object> resourceLocationValidator = s -> s instanceof String
            && ((String) s).matches("[a-z]+[:]{1}[a-z_]+");

        Client(WhiteNoiseConfigSpec.Builder builder) {
            disableAdvancementLocking = builder
                .comment(getTranslation("disableadvancementlocking"))
                .define("disableAdvancementLocking", false);

            noAdvancementBooks = builder
                .comment(getTranslation("noadvancementbooks"))
                .defineListAllowEmpty(List.of("noAdvancementBooks"), Collections::emptyList, resourceLocationValidator);

            testingMode = builder
                .comment(getTranslation("testingmode"))
                .define("testingMode", false);

            inventoryButtonBook = builder
                .comment(getTranslation("inventorybuttonbook"))
                .define("inventoryButtonBook", "", resourceLocationValidator);

            useShiftForQuickLookup = builder
                .comment(getTranslation("useshiftforquicklookup"))
                .define("useShiftForQuickLookup", false);

            overflowMode = builder
                .comment(getTranslation("textoverflowmode"))
                .defineEnum("textOverflowMode", TextOverflowMode.RESIZE);

            quickLookupTime = builder
                .comment(getTranslation("quicklookuptime"))
                .defineInRange("quickLookupTime", 10, 1, 20);
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

    private static String getTranslation(String key) {
        return Translations.get(key);
    }

}
