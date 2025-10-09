package guidebook.api.stub;

import java.io.InputStream;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import guidebook.api.IStyleStack;
import guidebook.api.GuidebookAPI.IGuidebookAPI;

public class StubGuidebookAPI implements IGuidebookAPI {

    public static final StubGuidebookAPI INSTANCE = new StubGuidebookAPI();

    private StubGuidebookAPI() {}

    @Override
    public void setConfigFlag(String flag, boolean value) {
        // NO-OP
    }

    @Override
    public boolean getConfigFlag(String flag) {
        return false;
    }

    @Override
    public void openBookGUI(ServerPlayer player, ResourceLocation book) {
        // NO-OP
    }

    @Override
    public void openBookEntry(ServerPlayer player, ResourceLocation book, ResourceLocation entry, int page) {

    }

    @Override
    public void openBookGUI(ResourceLocation book) {
        // NO-OP
    }

    @Override
    public void openBookEntry(ResourceLocation book, ResourceLocation entry, int page) {}

    @Override
    public ResourceLocation getOpenBookGui() {
        return null;
    }

    @Override
    public Component getSubtitle(ResourceLocation bookId) {
        throw new IllegalArgumentException("Guidebook is not loaded");
    }

    @Override
    public void registerCommand(String name, Function<IStyleStack, String> command) {
        // NO-OP
    }

    @Override
    public void registerFunction(String name, BiFunction<String, IStyleStack, String> function) {
        // NO-OP
    }

    @Override
    public ItemStack getBookStack(ResourceLocation book) {
        return ItemStack.EMPTY;
    }

    @Override
    public void registerTemplateAsBuiltin(ResourceLocation res, Supplier<InputStream> streamProvider) {
        // NO-OP
    }

}
