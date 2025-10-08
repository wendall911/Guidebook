package guidebook.common.command;

import java.util.Collection;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import guidebook.api.GuidebookAPI;
import guidebook.common.book.BookRegistry;

public class OpenBookCommand {
	private static final SuggestionProvider<CommandSourceStack> BOOK_ID_SUGGESTER =
			(ctx, builder) -> SharedSuggestionProvider.suggestResource(
					BookRegistry.INSTANCE.books.keySet(), builder);

	public static void register(CommandDispatcher<CommandSourceStack> disp) {
		disp.register(Commands.literal("open-guidebook-book")
				.requires(cs -> cs.hasPermission(2))
				.then(Commands.argument("targets", EntityArgument.players())
						.then(Commands.argument("book", ResourceLocationArgument.id())
								.suggests(BOOK_ID_SUGGESTER)
								.executes(ctx -> doIt(EntityArgument.getPlayers(ctx, "targets"),
										ResourceLocationArgument.getId(ctx, "book"),
										null, 0))
								.then(Commands.argument("entry", ResourceLocationArgument.id())
										.then(Commands.argument("page", IntegerArgumentType.integer(0))
												.executes(ctx -> doIt(EntityArgument.getPlayers(ctx, "targets"),
														ResourceLocationArgument.getId(ctx, "book"),
														ResourceLocationArgument.getId(ctx, "entry"),
														IntegerArgumentType.getInteger(ctx, "page"))))))));
	}

	private static int doIt(Collection<ServerPlayer> players, ResourceLocation book, @Nullable ResourceLocation entry, int page) {
		for (ServerPlayer player : players) {
			if (entry != null) {
				GuidebookAPI.get().openBookEntry(player, book, entry, page);
			} else {
				GuidebookAPI.get().openBookGUI(player, book);
			}
		}
		return players.size();
	}

}
