package handbook.common.command;

import java.util.Collection;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import handbook.api.HandbookAPI;
import handbook.common.book.BookRegistry;

public class OpenBookCommand {
    private static final SuggestionProvider<CommandSourceStack> BOOK_ID_SUGGESTER =
            (ctx, builder) -> SharedSuggestionProvider.suggestResource(
                    BookRegistry.INSTANCE.books.keySet(), builder);

    public static void register(CommandDispatcher<CommandSourceStack> disp) {
        disp.register(Commands.literal("open-handbook-book")
            .requires(Commands.hasPermission(Commands.LEVEL_ALL))
            .then(Commands.argument("targets", EntityArgument.players())
                .then(Commands.argument("book", IdentifierArgument.id())
                    .suggests(BOOK_ID_SUGGESTER)
                    .executes(ctx -> open(EntityArgument.getPlayers(ctx, "targets"),
                        IdentifierArgument.getId(ctx, "book"),
                        null, 0))
                    .then(Commands.argument("entry", IdentifierArgument.id())
                        .then(Commands.argument("page", IntegerArgumentType.integer(0))
                            .executes(ctx -> open(EntityArgument.getPlayers(ctx, "targets"),
                                IdentifierArgument.getId(ctx, "book"),
                                IdentifierArgument.getId(ctx, "entry"),
                                IntegerArgumentType.getInteger(ctx, "page"))))))));
    }

    private static int open(Collection<ServerPlayer> players, Identifier book, @Nullable Identifier entry,
            int page) {
        for (ServerPlayer player : players) {
            if (entry != null) {
                HandbookAPI.get().openBookEntry(player, book, entry, page);
            }
            else {
                HandbookAPI.get().openBookGUI(player, book);
            }
        }

        return players.size();
    }

}
