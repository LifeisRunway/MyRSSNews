package com.imra.mynews.app;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Date: 27.07.2019
 * Time: 17:24
 *
 * @author IMRA027
 */

public class MyNewsError extends Throwable {

    public MyNewsError (ResponseBody responseBody) {super(getMessage(responseBody));}

    public static String getMessage (ResponseBody responseBody) {
        try {
            return new JSONObject(responseBody.string()).optString("message");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return "Unknown exception";
    }

}
