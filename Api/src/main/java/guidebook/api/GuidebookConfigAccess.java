package guidebook.api;

import java.util.List;

public interface GuidebookConfigAccess {

	boolean disableAdvancementLocking();

	List<String> noAdvancementBooks();

	boolean testingMode();

	String inventoryButtonBook();

	boolean useShiftForQuickLookup();

	TextOverflowMode overflowMode();

	int quickLookupTime();

	enum TextOverflowMode {
		OVERFLOW,
		TRUNCATE,
		RESIZE
	}

}
