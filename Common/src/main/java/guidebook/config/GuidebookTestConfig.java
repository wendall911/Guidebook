package guidebook.config;

import org.apache.commons.lang3.tuple.Pair;

import technology.roughness.whitenoise.config.WhiteNoiseConfigSpec;

public class GuidebookTestConfig {

    public static final WhiteNoiseConfigSpec COMMON_SPEC;
    public static final Test CLIENT_TEST;

    static {
        Pair<Test, WhiteNoiseConfigSpec> specPair = new WhiteNoiseConfigSpec.Builder().configure(Test::new);
        COMMON_SPEC = specPair.getRight();
        CLIENT_TEST = specPair.getLeft();
    }

    public static class Test {
        public final WhiteNoiseConfigSpec.BooleanValue giveBook;

        public Test(WhiteNoiseConfigSpec.Builder builder) {
            giveBook = builder
                .comment("Give player book on first join.")
                .define("giveBook", true);
        }
    }

}
