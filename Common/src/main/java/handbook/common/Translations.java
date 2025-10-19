package handbook.common;

import java.util.Map;

import com.google.common.collect.Maps;

public class Translations {

    private static final Map<String, String> translations = Maps.newHashMap();

    static {
        translations.put("disableadvancementlocking.title", "Disable Advancement Locking");
        translations.put(
            "disableadvancementlocking",
            "Set to on/true to disable advancement locking for ALL books, " +
                "making all entries visible at all times. Config Flag: advancements_disabled"
        );
        translations.put("noadvancementbooks.title", "No Advancement Books");
        translations.put(
            "noadvancementbooks",
            "Granular list of Book ID's to disable advancement locking for, " +
                "e.g. [ \"survivalistessentials:lexicon\" ]. Config Flags: advancements_disabled_<bookid>"
        );
        translations.put("testingmode.title", "Testing Mode");
        translations.put(
            "testingmode",
            "Set to on/true to enable testing mode. By default this doesn't do anything, but you can use the config " +
                "flag in your books if you want. Config Flag: testing_mode"
        );
        translations.put("inventorybuttonbook.title", "Inventory Button Book");
        translations.put(
            "inventorybuttonbook",
            "Set this to the ID of a book to have it show up in players' inventories, " +
                "replacing the recipe book.");
        translations.put("useshiftforquicklookup.title", "Use Shift for Quick Lookup");
        translations.put(
            "useshiftforquicklookup",
            "Set to on/true to use Shift instead of Ctrl " +
                "for the inventory quick lookup feature.");
        translations.put("textoverflowmode.title", "Text Overflow Mode");
        translations.put(
            "textoverflowmode",
            "Set how text overflow should be coped with: overflow the text off the page, " +
                "truncate overflowed text, or resize everything to fit. Reload world after changing."
        );
        translations.put("quicklookuptime.title", "Quick Lookup Time");
        translations.put(
            "quicklookuptime",
            "How long in ticks the quick lookup key needs to be pressed before the book opens"
        );
    }

    public static String get(String key) {
        return translations.getOrDefault(key, key);
    }

}
