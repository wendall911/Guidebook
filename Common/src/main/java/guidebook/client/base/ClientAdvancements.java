package guidebook.client.base;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import guidebook.client.book.ClientBookRegistry;
import guidebook.common.book.Book;
import guidebook.common.util.ColorHelper.GuidebookColors;
import guidebook.mixin.client.AccessorClientAdvancements;

public class ClientAdvancements {

	private static boolean gotFirstAdvPacket = false;

	/* Hooked at the end of ClientAdvancementManager.read, when the advancement packet arrives clientside
	The initial book load is done here when the first advancement packet arrives.
	Doing it anytime before that leads to excessive toast spam because the book believes everything to be locked,
	and then the first advancement packet unlocks everything.
	*/
	public static void onClientPacket() {
		if (!gotFirstAdvPacket) {
			ClientBookRegistry.INSTANCE.reload();
			gotFirstAdvPacket = true;
		} else {
			ClientBookRegistry.INSTANCE.reloadLocks(false);
		}
	}

	public static boolean hasDone(String advancement) {
		ResourceLocation id = ResourceLocation.tryParse(advancement);
		if (id != null) {
			ClientPacketListener conn = Minecraft.getInstance().getConnection();
			if (conn != null) {
				net.minecraft.client.multiplayer.ClientAdvancements cm = conn.getAdvancements();
				AdvancementHolder adv = cm.get(id);
				if (adv != null) {
					Map<AdvancementHolder, AdvancementProgress> progressMap = ((AccessorClientAdvancements) cm).getProgress();
					AdvancementProgress progress = progressMap.get(adv);
					return progress != null && progress.isDone();
				}
			}
		}
		return false;
	}

	public static void playerLogout() {
		gotFirstAdvPacket = false;
	}

	public static void sendBookToast(Book book) {
        ToastManager toastManager = Minecraft.getInstance().getToastManager();

        if (toastManager.getToast(LexiconToast.class, book) == null) {
            toastManager.addToast(new LexiconToast(book));
		}
	}

	public static class LexiconToast implements Toast {
		private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");
		private final Book book;
        private Toast.Visibility visibility = Toast.Visibility.SHOW;

		public LexiconToast(Book book) {
			this.book = book;
		}

		@NotNull
		@Override
		public Book getToken() {
			return book;
		}

        @Override
        public @NotNull Visibility getWantedVisibility() {
            return visibility;
        }

        @Override
        public void update(@NotNull ToastManager toastManager, long delta) {
            if (delta >= 5000L) {
                visibility = Toast.Visibility.HIDE;
            }
        }

        @Override
		public void render(GuiGraphics graphics, @NotNull Font font, long delta) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, width(), height());

			graphics.drawString(
                font,
                Component.translatable(book.name),
                30,
                7,
                GuidebookColors.ADVANCEMENT.toColor(),
                false
            );
			graphics.drawString(
                font,
                Component.translatable("guidebook.gui.lexicon.toast.info"),
                30,
                17,
                GuidebookColors.WHITE.toColor(),
                false
            );

			graphics.renderItem(book.getBookItem(), 8, 8);
			graphics.renderItemDecorations(font, book.getBookItem(), 8, 8);

		}

	}

}
