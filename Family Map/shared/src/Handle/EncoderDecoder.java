package Handle;

import com.google.gson.*;

public class EncoderDecoder {
    private static final Gson gson = new Gson();

    /**
     * Creates and returns object from json String
     * @param jsonString
     * @param o
     * @return
     */
    public static Object encode(String jsonString, Object o) {
        return gson.fromJson(jsonString, o.getClass());
    }

    /**
     *  Creates and returns json String from object
     * @param o
     * @return
     */
    public static String decode(Object o) {
        return gson.toJson(o);
    }
}
