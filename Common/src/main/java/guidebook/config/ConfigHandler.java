package guidebook.config;

import java.util.Collections;
import java.util.List;

import net.minecraft.resources.ResourceLocation;

import guidebook.api.GuidebookConfigAccess;
import guidebook.common.base.GuidebookConfig;

public class ConfigHandler {

	public static final WhiteNoiseConfigSpec.ConfigValue<Boolean> disableAdvancementLocking;
	public static final WhiteNoiseConfigSpec.ConfigValue<List<? extends String>> noAdvancementBooks;
	public static final WhiteNoiseConfigSpec.ConfigValue<Boolean> testingMode;
	public static final WhiteNoiseConfigSpec.ConfigValue<String> inventoryButtonBook;
	public static final WhiteNoiseConfigSpec.ConfigValue<Boolean> useShiftForQuickLookup;
	public static final WhiteNoiseConfigSpec.EnumValue<GuidebookConfigAccess.TextOverflowMode> overflowMode;
	public static final WhiteNoiseConfigSpec.ConfigValue<Integer> quickLookupTime;

	private static final WhiteNoiseConfigSpec SPEC;

	static {
		WhiteNoiseConfigSpec.Builder builder = new WhiteNoiseConfigSpec.Builder();
		disableAdvancementLocking = builder
				.comment("Set this to true to disable advancement locking for ALL books, making all entries visible at all times. Config Flag: advancements_disabled")
				.define("disableAdvancementLocking", false);

		noAdvancementBooks = builder
				.comment("Granular list of Book ID's to disable advancement locking for, e.g. [ \"botania:lexicon\" ]. Config Flags: advancements_disabled_<bookid>")
				.defineListAllowEmpty(List.of("noAdvancementBooks"), Collections::emptyList,
						o -> o instanceof String s && ResourceLocation.tryParse(s) != null);

		testingMode = builder
				.comment("Enable testing mode. By default this doesn't do anything, but you can use the config flag in your books if you want. Config Flag: testing_mode")
				.define("testingMode", false);

		inventoryButtonBook = builder
				.comment("Set this to the ID of a book to have it show up in players' inventories, replacing the recipe book.")
				.define("inventoryButtonBook", "");

		useShiftForQuickLookup = builder
				.comment("Set this to true to use Shift instead of Ctrl for the inventory quick lookup feature.")
				.define("useShiftForQuickLookup", false);

		overflowMode = builder
				.comment("Set how text overflow should be coped with: overflow the text off the page, truncate overflowed text, or resize everything to fit. Relogin after changing.")
				.defineEnum("textOverflowMode", GuidebookConfigAccess.TextOverflowMode.RESIZE);

		quickLookupTime = builder
				.comment("How long in ticks the quick lookup key needs to be pressed before the book opens")
				.define("quickLookupTime", 10);

		SPEC = builder.build();
	}

	public static void setup(ModContainer container) {
		container.registerConfig(WhiteNoiseConfig.Type.CLIENT, SPEC);
		GuidebookConfig.set(new GuidebookConfigAccess() {
			@Override
			public boolean disableAdvancementLocking() {
				return disableAdvancementLocking.get();
			}

			@Override
			public List<String> noAdvancementBooks() {
				// cast from List<? extends String> to List<String>
				// String is final so this is safe
				// This is only needed because the Config API is stupid and forces a `? extends` type.
				return (List<String>) noAdvancementBooks.get();
			}

			@Override
			public boolean testingMode() {
				return testingMode.get();
			}

			@Override
			public String inventoryButtonBook() {
				return inventoryButtonBook.get();
			}

			@Override
			public boolean useShiftForQuickLookup() {
				return useShiftForQuickLookup.get();
			}

			@Override
			public TextOverflowMode overflowMode() {
				return overflowMode.get();
			}

			@Override
			public int quickLookupTime() {
				return quickLookupTime.get();
			}
		});
	}
}
