package handbook.common.item;

import java.util.function.BiConsumer;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;

import handbook.api.HandbookAPI;

public class HandbookDataComponents {

    public static final Identifier COMPONENT_ID = Identifier.fromNamespaceAndPath(HandbookAPI.MODID, "book");
    public static final DataComponentType<Identifier> BOOK = DataComponentType.<Identifier>builder()
        .persistent(Identifier.CODEC)
        .networkSynchronized(Identifier.STREAM_CODEC)
        .build();

    public static void submitDataComponentRegistrations(BiConsumer<Identifier, DataComponentType<?>> consumer) {
        consumer.accept(COMPONENT_ID, BOOK);
    }

}
