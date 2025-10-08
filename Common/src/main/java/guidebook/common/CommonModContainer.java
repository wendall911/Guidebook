package guidebook.common;

import java.nio.file.Path;
import java.util.List;

/**
 * Small cross-loader abstraction over mod containers
 */
public interface CommonModContainer {

	String getId();
	String getName();
	Path getPath(String s);
	List<Path> getRootPaths();

}
