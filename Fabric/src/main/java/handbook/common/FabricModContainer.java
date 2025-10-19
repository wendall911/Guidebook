package handbook.common;

import java.nio.file.Path;
import java.util.List;

import net.fabricmc.loader.api.ModContainer;

public class FabricModContainer implements CommonModContainer {
    private final ModContainer container;

    public FabricModContainer(ModContainer container) {
        this.container = container;
    }

    @Override
    public String getId() {
        return container.getMetadata().getId();
    }

    @Override
    public String getName() {
        return container.getMetadata().getName();
    }

    @Override
    public Path getPath(String file) {
        return container.getPath(file);
    }

    @Override
    public List<Path> getRootPaths() {
        return container.getRootPaths();
    }

}
