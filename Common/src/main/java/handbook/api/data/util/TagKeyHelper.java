package handbook.api.data.util;

import net.minecraft.tags.TagKey;

public class TagKeyHelper {

    public static String serializeTagKey(TagKey<?> tag) {
        return "tag:" + tag.location();
    }

}
