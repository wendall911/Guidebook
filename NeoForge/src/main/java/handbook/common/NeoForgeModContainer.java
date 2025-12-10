package handbook.common;

import java.nio.file.Path;
import java.util.List;

import net.neoforged.fml.ModContainer;

public class NeoForgeModContainer implements CommonModContainer {
    private final ModContainer container;

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
        Path resources = getRootPaths().stream().filter(
            path -> path.resolve(s).toFile().exists()
        ).findFirst().orElse(null);

        if (resources != null) {
            return resources.resolve(s);
        }

        return null;
    }

    @Override
    public List<Path> getRootPaths() {
        return container.getModInfo().getOwningFile().getFile().getContents().getContentRoots().stream().filter(
            p -> p.toString().contains("resources")
        ).toList();
    }

}
