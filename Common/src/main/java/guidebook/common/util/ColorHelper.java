package guidebook.common.util;

public class ColorHelper extends technology.roughness.whitenoise.util.ColorHelper {

    public static int getGuidebookColor(String hexColor) {
        if (!hexColor.startsWith("#")) {
            hexColor = "#" + hexColor;
        }

        return ColorHelper.hexToRGBA(hexColor);
    }

    public static int getSecret(int color) {
        return 0xAA000000 | (color & 0x00FFFFFF);
    }

    public static int getLocked(int color) {
        return 0x77000000 | (color & 0x00FFFFFF);
    }

    public static int alphaMask(int color, float alpha) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255)));

        return (a << 24) | (color & 0x00FFFFFF);
    }

    public static int fillBlack(float alpha) {
        return alphaMask(GuidebookColors.BLACK.toColor(), alpha);
    }

    public enum GuidebookColors {

        ADVANCEMENT("#F000F0"),
        BLACK(Colors.BLACK.getHex()),
        BOOKMARK_READ("#00FF01"),
        BOOK_EYE("#FF3333"),
        COMPLETE("#008B1A"),
        ERROR_GRAY("#777777"),
        ERROR_RED(Colors.ERROR_RED.getHex()),
        GRAY("#BBBBBB"),
        HEADER("#333333"),
        LEXICON_SAD("#999999"),
        LINK("#0000EE"),
        LINK_HOVER("#8800EE"),
        NAMEPLATE("#FFDD00"),
        PACKED_LIGHT("#F000F0"),
        PROGRESS_BAR(Colors.YELLOW.getHex()),
        PROGRESS_BAR_BACKGROUND("#DDDDDD"),
        TEXT(Colors.BLACK.getHex()),
        WHITE(Colors.WHITE.getHex());

        private final String hex;

        GuidebookColors(String hex) {
            this.hex = hex;
        }

        public String getHex() {
            return hex;
        }

        /*
         * Returns the color as an integer in RGBA format.
         */
        public int toColor() {
            return ColorHelper.hexToRGBA(this.hex);
        }

        /*
         * Returns the color as an integer in RGBA format.
         */
        public int toAlphaColor(float alpha) {
            return alphaMask(ColorHelper.hexToRGBA(this.hex), alpha);
        }

    }

}
