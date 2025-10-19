package handbook.api;

import java.io.InputStream;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Suppliers;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import handbook.api.stub.StubHandbookAPI;

import static technology.roughness.whitenoise.util.ResourceLocationHelper.loc;

public class HandbookAPI {

    private static final Supplier<IHandbookAPI> LAZY_INSTANCE = Suppliers.memoize(() -> {
        try {
            return (IHandbookAPI) Class.forName("handbook.common.base.HandbookAPIImpl").newInstance();
        }
        catch (ReflectiveOperationException e) {
            LoggerFactory.getLogger("Handbook").warn("Unable to find HandbookAPIImpl, using a dummy");

            return StubHandbookAPI.INSTANCE;
        }
    });

    /**
     * Obtain the Handbook API, either a valid implementation if Handbook is present, else
     * a no-op stub instance if Handbook is absent.
     * Note that many API functions make no sense to call before books are loaded (which is on login),
     * please use common sense and your best judgment.
     */
    public static IHandbookAPI get() {
        return LAZY_INSTANCE.get();
    }

    public static final String MODID = "handbook";
    public static final String MOD_NAME = "Handbook";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static ResourceLocation prefix(String path) {
        return loc(MODID, path);
    }

    public interface IHandbookAPI {

        /**
         * Sets a config flag to the value passed.<br>
         * This is safe to call during parallel mod loading.<br>
         * IMPORTANT: DO NOT call this without your flag being prefixed with your
         * mod id. There is no protection against that, but don't be a jerk.
         */
        void setConfigFlag(String flag, boolean value);

        /**
         * Gets the value of a config flag, or false if it doesn't have a value.
         */
        boolean getConfigFlag(String flag);

        /**
         * Sends a network message to the given player
         * to open the given book to the last page that was open, or the landing page otherwise.
         */
        void openBookGUI(ServerPlayer player, ResourceLocation book);

        /**
         * Sends a network message to the given player
         * to open the book to the given entry
         *
         * @param page Zero-indexed page number
         */
        void openBookEntry(ServerPlayer player, ResourceLocation book, ResourceLocation entry, int page);

        /**
         * Client version of {@link #openBookGUI(ServerPlayer, ResourceLocation)}.
         */
        void openBookGUI(ResourceLocation book);

        /**
         * Client version of {@link #openBookEntry(ServerPlayer, ResourceLocation, ResourceLocation, int)}
         */
        void openBookEntry(ResourceLocation book, ResourceLocation entry, int page);

        /**
         * Returns the book ID of the currently open book, if any. Only works clientside.
         */
        @Nullable
        ResourceLocation getOpenBookGui();

        /**
         * Works on both sides.
         *
         * @return                          The subtitle (edition string/what appears under the title in the landing
         *                                  page) of the book.
         * @throws IllegalArgumentException if the book id given cannot be found
         */
        Component getSubtitle(ResourceLocation bookId);

        /**
         * Returns a book item with its NBT set to the book passed in. Works on both sides.
         */
        ItemStack getBookStack(ResourceLocation book);

        /**
         * Register a template you made as a built in template to be used with all books
         * as the "res" resource location. The supplier should give an input stream that
         * reads a full json file, containing a template.
         * Only works on client.
         */
        void registerTemplateAsBuiltin(ResourceLocation res, Supplier<InputStream> streamProvider);

        /**
         * Register a Handbook command, of the type $(cmdname).
         * A command gets an IStyleStack if it wishes to modify it (for example, $(o) italicizes),
         * and returns the text that should replace the command (for example, $(playername) is replaced
         * with the current player's username). Commands that only modify style should return "".
         * This is thread safe. Only works on client.
         */
        void registerCommand(String name, Function<IStyleStack, String> command);

        /**
         * Register a Handbook function, of the type $(funcname:arg).
         * A function is like a command,
         * except it gets an additional argument (the text after the colon),
         * for things like conditional formatting or differing return values.
         * For example, $(k:use) is replaced by Right Button by default.
         * This is thread safe. Only works on client.
         */
        void registerFunction(String name, BiFunction<String, IStyleStack, String> function);

    }

}
