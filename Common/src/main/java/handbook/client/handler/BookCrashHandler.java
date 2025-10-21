package handbook.client.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Supplier;

import net.minecraft.SystemReport;
import net.minecraft.client.Minecraft;

import handbook.api.HandbookAPI;
import handbook.client.book.gui.GuiBook;
import handbook.client.book.gui.GuiBookEntry;
import handbook.client.book.gui.GuiBookEntryList;
import handbook.common.book.Book;

public class BookCrashHandler implements Supplier<String> {
    private static final String INDENT = "\n\t\t";
    private static final String LABEL = "Handbook open book context";

    public static void appendToCrashReport(SystemReport report) {
        Minecraft mc = Minecraft.getInstance();

        if (mc == null) {
            return;
        }

        if (mc.screen != null && !(mc.screen instanceof GuiBook)) {
            return;
        }

        try {
            report.setDetail(LABEL, new BookCrashHandler());
        }
        catch (Exception e) {
            HandbookAPI.LOGGER.error("Failed to extend crash report system info", e);
        }
    }

    @Override
    public String get() {
        Minecraft mc = Minecraft.getInstance();
        String errorText = "n/a";

        if (mc == null) {
            return errorText;
        }

        if (mc.screen != null && !(mc.screen instanceof GuiBook gui)) {
            return errorText;
        }

        GuiBook gui = (GuiBook) mc.screen;

        if (gui == null || gui.book == null) {
            return "Book GUI or book is null. Book not registered properly.";
        }

        Book book = gui.book;
        StringBuilder builder = new StringBuilder(INDENT);

        builder.append("Open book: ").append(book.id);

        if (gui instanceof GuiBookEntry entry) {
            builder.append(INDENT).append("Current entry: ").append(entry.getEntry().getId());
        }
        else if (gui instanceof GuiBookEntryList list) {
            builder.append(INDENT).append("Search query: ").append(list.getSearchQuery());
        }

        builder.append(INDENT).append("Current page spread: ").append(gui.getSpread());

        if (book.getContents().isErrored()) {
            Exception ex = book.getContents().getException();
            builder.append(INDENT).append("Book loading error: ");

            try (StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw)) {
                if (ex == null) {
                    pw.println("Unknown error");
                }
                else {
                    ex.printStackTrace(pw);
                }
                builder.append(sw.toString().replaceAll("\n", INDENT));
            }
            catch (IOException ignored) {}
        }

        return builder.toString();
    }

}
