package handbook.common;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforgespi.locating.IModFile;

import handbook.api.HandbookAPI;

public class NeoForgeModContainer implements CommonModContainer {

    private final ModContainer container;
    private final Map<String, FileSystem> fileSystems = new HashMap<>();

    public NeoForgeModContainer(ModContainer container) {
        this.container = container;
    }

    @Override
    public String getId() {
        return container.getModId();
    }

    @Override
    public String getName() {
        return container.getModInfo().getDisplayName();
    }

    @Override
    public Path getPath(String s) {
        if (fileSystems.containsKey(container.getModId())) {
            FileSystem fs = fileSystems.get(container.getModId());
            Path path = fs.getPath("/").resolve(s);
            if (Files.exists(path)) {
                return path;
            }
        }

        Path resources = getRootPaths().stream().filter(
            path -> path.resolve(s).toFile().exists()
        ).findFirst().orElse(null);

        if (resources != null) {
            return resources.resolve(s);
        }

        return null;
    }

    /*
     * NeoForge no longer provides a virtual filesystem for jar files.
     * We need to check if this is a jar file and create our own filesystem if it is.
     * If it's not a jar file, we can just return the root paths and let the caller handle it.
     * This is a bit of a hack, but it should work for now.
     * We should probably look into a better solution in the future.
     */
    @Override
    public List<Path> getRootPaths() {
        IModFile file = container.getModInfo().getOwningFile().getFile();

        if (file.getFilePath().getFileName().toString().contains(".jar")) {
            try {
                FileSystem fs = FileSystems.newFileSystem(file.getFilePath());

                fileSystems.put(container.getModId(), fs);

                return List.of(fs.getRootDirectories().iterator().next().getRoot());
            }
            catch (IOException e) {
                HandbookAPI.LOGGER.error("Failed to read mod file system for mod {}", getId(), e);
                return Collections.emptyList();
            }
        }
        else {
            List<Path> paths = file.getContents().getContentRoots().stream().filter(
                p -> p.toString().contains("resources")
            ).toList();

            return paths;
        }
    }

}
