package handbook.common;

import java.nio.file.Path;
import java.util.Collections;
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
        return container.getModInfo().getOwningFile().getFile().findResource(s);
    }

    @Override
    public List<Path> getRootPaths() {
        return Collections.singletonList(container.getModInfo().getOwningFile().getFile().getSecureJar().getRootPath());
    }

}
