package jingruichen.mini_editor;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.*;

public class PresistenceSave {
    /**
     * using to save instance class or any other data type
     * @param context operateing page
     * @param key save key
     * @param obj the saving object
     */
    public static void putBean(Context context, String key, Object obj) {
        if (obj instanceof Serializable) {
            // obj must implement Serializable interface, otherwise there will be problems
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);
                String string64 = new String(Base64.encode(baos.toByteArray(), 0));

                SharedPreferences.Editor editor = (context.getSharedPreferences("list",Context.MODE_PRIVATE)).edit();
                editor.putString(key, string64).commit();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            throw new IllegalArgumentException(
                    "the obj must implement Serializable");
        }

    }

    public static Object getBean(Context context, String key) {
        Object obj = null;
        try {
            String base64 = context.getSharedPreferences("list", Context.MODE_PRIVATE).getString(key, "");
            if (base64.equals("")) {
                return null;
            }
            byte[] base64Bytes = Base64.decode(base64.getBytes(), 1);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            obj = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}
