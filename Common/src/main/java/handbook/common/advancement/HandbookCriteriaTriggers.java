package handbook.common.advancement;

import java.util.function.BiConsumer;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.Identifier;

public class HandbookCriteriaTriggers {

    public static final BookOpenTrigger BOOK_OPEN = new BookOpenTrigger();

    public static void submitTriggerRegistrations(BiConsumer<Identifier, CriterionTrigger<?>> consumer) {
        consumer.accept(BookOpenTrigger.ID, BOOK_OPEN);
    }

}
