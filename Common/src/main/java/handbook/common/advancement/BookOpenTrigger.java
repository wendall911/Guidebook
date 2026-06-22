package handbook.common.advancement;

import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import handbook.api.HandbookAPI;
import handbook.common.advancement.BookOpenTrigger.TriggerInstance;

/**
 * An advancement trigger for opening Handbook books.
 */
public class BookOpenTrigger extends SimpleCriterionTrigger<TriggerInstance> {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(HandbookAPI.MODID, "open_book");
    public static final BookOpenTrigger INSTANCE = new BookOpenTrigger();

    @NonNull
    @Override
    public Codec<TriggerInstance> codec() {
        return BookOpenTrigger.TriggerInstance.CODEC;
    }

    public void trigger(@NonNull ServerPlayer player, @NonNull Identifier book) {
        trigger(player, instance -> instance.matches(book, null, 0));
    }

    public void trigger(@NonNull ServerPlayer player, @NonNull Identifier book, @Nullable Identifier entry, int page) {
        trigger(player, instance -> instance.matches(book, entry, page));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Identifier book,
                                  Optional<Identifier> entry, MinMaxBounds.Ints page) implements SimpleInstance {
        public static Codec<BookOpenTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
            Identifier.CODEC.fieldOf("book").forGetter(TriggerInstance::book),
            Identifier.CODEC.optionalFieldOf("entry").forGetter(TriggerInstance::entry),
            MinMaxBounds.Ints.CODEC.optionalFieldOf("page", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::page)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(@NonNull Identifier book, @Nullable Identifier entry, int page) {
            return this.book.equals(book) && (this.entry.isEmpty() || this.entry.get().equals(entry)) && this.page.matches(page);
        }
    }

}
