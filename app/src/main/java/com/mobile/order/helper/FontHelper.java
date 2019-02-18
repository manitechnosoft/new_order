package com.mobile.order.helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Description: Helper class to manage the fonts
 * Author: Cerlin <cerlin@linkstreet.in>.
 * Date: 22/4/17
 */

public class FontHelper {

    private FontHelper() {
        //Empty constructor
    }

    /**
     * HashMap to hold all the fonts using string identifier.
     */
    private static HashMap<String, Typeface> fonts;

    /**
     * Method to load all the fonts which are needed.
     *
     * @param context App context in which the fonts will be loaded.
     */
    public static synchronized void loadFonts(Context context) {
        if (fonts == null) {
            fonts = new HashMap<>();
            AssetManager manager = context.getAssets();

            fonts.put(Fonts.ALEGREYA_REGULAR, prepareFont(manager, "fonts/Alegreya/Alegreya-Regular.ttf"));
            fonts.put(Fonts.MULI_BOLD, prepareFont(manager, "fonts/Muli/Muli-Bold.ttf"));
            fonts.put(Fonts.MULI_EXTRA_BOLD, prepareFont(manager, "fonts/Muli/Muli-ExtraBold.ttf"));
            fonts.put(Fonts.MULI_LIGHT, prepareFont(manager, "fonts/Muli/Muli-Light.ttf"));
            fonts.put(Fonts.MULI_REGULAR, prepareFont(manager, "fonts/Muli/Muli-Regular.ttf"));
            fonts.put(Fonts.MULI_SEMI_BOLD, prepareFont(manager, "fonts/Muli/Muli-SemiBold.ttf"));
            fonts.put(Fonts.SATISFY_REGULAR, prepareFont(manager, "fonts/Satisfy/Satisfy-Regular.ttf"));
            fonts.put(Fonts.SHEPPARDS_REGULAR, prepareFont(manager, "fonts/MrsSheppards/MrsSheppards-Regular.ttf"));
            fonts.put(Fonts.SHORT_STACK_REGULAR, prepareFont(manager, "fonts/ShortStack/ShortStack-Regular.ttf"));
        }
    }

    /**
     * Public method to get the font.
     *
     * @param flag Identifier for the flag. {@link Fonts}
     * @return Typeface
     */
    public static Typeface getFont(String flag) {
        return fonts.get(flag);
    }

    /**
     * Create Typeface object for the given font
     *
     * @param manager AssetManager
     * @param path    Path to font
     * @return Typeface
     */
    private static Typeface prepareFont(AssetManager manager, String path) {
        return Typeface.createFromAsset(manager, path);
    }
}
