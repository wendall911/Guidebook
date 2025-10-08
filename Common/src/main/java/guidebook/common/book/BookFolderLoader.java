package guidebook.common.book;

import java.io.File;
import java.io.FileInputStream;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

import guidebook.api.GuidebookAPI;
import guidebook.common.IBookHelper;
import guidebook.common.CommonModContainer;

public class BookFolderLoader {

	public static File loadDir;

	private static void setup() {
		loadDir = new File(BookRegistry.BOOKS_LOCATION);
		if (!loadDir.exists()) {
			loadDir.mkdir();
		} else if (!loadDir.isDirectory()) {
			throw new RuntimeException(loadDir.getAbsolutePath() + " is a file, not a folder, aborting. Please delete this file or move it elsewhere if it has important contents.");
		}
	}

	public static void findBooks() {
		if (loadDir == null) {
			setup();
		}

		CommonModContainer self = IBookHelper.INSTANCE.getModContainer(GuidebookAPI.MODID);
		File[] subdirs = loadDir.listFiles(File::isDirectory);
		if (subdirs == null) {
			GuidebookAPI.LOGGER.warn("Failed to list external books in {}, not loading external books",
					loadDir.getAbsolutePath());
			return;
		}

		for (File dir : subdirs) {
			ResourceLocation res;
			try {
				res = ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, dir.getName());
			} catch (ResourceLocationException ex) {
				GuidebookAPI.LOGGER.error("Invalid external book folder name {}, skipping", dir.getName(), ex);
				continue;
			}

			File bookJson = new File(dir, "book.json");
			try (FileInputStream stream = new FileInputStream(bookJson)) {
				BookRegistry.INSTANCE.loadBook(self, res, stream, true);
			} catch (Exception e) {
				GuidebookAPI.LOGGER.error("Failed to load external book json from {}, skipping", bookJson, e);
			}
		}
	}

}
