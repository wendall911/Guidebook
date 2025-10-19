package handbook.common.base;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import org.apache.commons.io.IOUtils;

import handbook.api.HandbookAPI.IHandbookAPI;
import handbook.api.IStyleStack;
import handbook.client.book.BookContents;
import handbook.client.book.ClientBookRegistry;
import handbook.client.book.gui.GuiBook;
import handbook.client.book.template.BookTemplate;
import handbook.client.book.text.BookTextParser;
import handbook.common.advancement.BookOpenTrigger;
import handbook.common.book.Book;
import handbook.common.book.BookRegistry;
import handbook.common.item.HandbookBook;
import handbook.config.HandbookConfig;
import handbook.platform.Services;

import static technology.roughness.whitenoise.platform.Services.PLATFORM;

public class HandbookAPIImpl implements IHandbookAPI {

    private static void assertPhysicalClient() {
        Preconditions.checkState(
            PLATFORM.isPhysicalClient(),
            "Not on the physical client"
        );
    }

    @Override
    public void setConfigFlag(String flag, boolean value) {
        HandbookConfig.setFlag(flag, value);
    }

    @Override
    public boolean getConfigFlag(String flag) {
        return HandbookConfig.getConfigFlag(flag);
    }

    @Override
    public void openBookGUI(ServerPlayer player, ResourceLocation book) {
        BookOpenTrigger.INSTANCE.trigger(player, book);
        Services.BOOK_HELPER.sendOpenBookGui(player, book, null, 0);
    }

    @Override
    public void openBookEntry(ServerPlayer player, ResourceLocation book, ResourceLocation entry, int page) {
        BookOpenTrigger.INSTANCE.trigger(player, book, entry, page);
        Services.BOOK_HELPER.sendOpenBookGui(player, book, entry, page);
    }

    @Override
    public void openBookGUI(ResourceLocation book) {
        assertPhysicalClient();
        ClientBookRegistry.INSTANCE.displayBookGui(book, null, 0);
    }

    @Override
    public void openBookEntry(ResourceLocation book, ResourceLocation entry, int page) {
        assertPhysicalClient();
        ClientBookRegistry.INSTANCE.displayBookGui(book, entry, page);
    }

    @Override
    public ResourceLocation getOpenBookGui() {
        assertPhysicalClient();
        Screen gui = Minecraft.getInstance().screen;
        if (gui instanceof GuiBook) {
            return ((GuiBook) gui).book.id;
        }
        return null;
    }

    @NotNull
    @Override
    public Component getSubtitle(@NotNull ResourceLocation bookId) {
        Book book = BookRegistry.INSTANCE.books.get(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found: " + bookId);
        }
        return book.getSubtitle();
    }

    @Override
    public void registerCommand(String name, Function<IStyleStack, String> command) {
        assertPhysicalClient();
        BookTextParser.register(command::apply, name);
    }

    @Override
    public void registerFunction(String name, BiFunction<String, IStyleStack, String> function) {
        assertPhysicalClient();
        BookTextParser.register(function::apply, name);
    }

    @Override
    public ItemStack getBookStack(ResourceLocation book) {
        return HandbookBook.forBook(book);
    }

    @Override
    public void registerTemplateAsBuiltin(ResourceLocation res, Supplier<InputStream> streamProvider) {
        assertPhysicalClient();
        InputStream testStream = streamProvider.get();
        if (testStream == null) {
            throw new NullPointerException("Stream provider can't return a null stream");
        }
        IOUtils.closeQuietly(testStream);

        Supplier<BookTemplate> prev = BookContents.addonTemplates.put(res, () -> {
            InputStream stream = streamProvider.get();
            InputStreamReader reader = new InputStreamReader(stream);

            return ClientBookRegistry.INSTANCE.gson.fromJson(reader, BookTemplate.class);
        });

        if (prev != null) {
            throw new IllegalArgumentException("Template " + res + " is already registered");
        }
    }

}
