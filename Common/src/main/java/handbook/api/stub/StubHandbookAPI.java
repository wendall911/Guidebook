package handbook.api.stub;

import java.io.InputStream;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import handbook.api.IStyleStack;
import handbook.api.HandbookAPI.IHandbookAPI;

public class StubHandbookAPI implements IHandbookAPI {

    public static final StubHandbookAPI INSTANCE = new StubHandbookAPI();

    private StubHandbookAPI() {}

    @Override
    public void setConfigFlag(String flag, boolean value) {
        // NO-OP
    }

    @Override
    public boolean getConfigFlag(String flag) {
        return false;
    }

    @Override
    public void openBookGUI(ServerPlayer player, Identifier book) {
        // NO-OP
    }

    @Override
    public void openBookEntry(ServerPlayer player, Identifier book, Identifier entry, int page) {

    }

    @Override
    public void openBookGUI(Identifier book) {
        // NO-OP
    }

    @Override
    public void openBookEntry(Identifier book, Identifier entry, int page) {}

    @Override
    public Identifier getOpenBookGui() {
        return null;
    }

    @Override
    public Component getSubtitle(Identifier bookId) {
        throw new IllegalArgumentException("Handbook is not loaded");
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
    public ItemStack getBookStack(Identifier book) {
        return ItemStack.EMPTY;
    }

    @Override
    public void registerTemplateAsBuiltin(Identifier res, Supplier<InputStream> streamProvider) {
        // NO-OP
    }

}
