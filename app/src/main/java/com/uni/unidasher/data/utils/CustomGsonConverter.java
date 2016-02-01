package com.uni.unidasher.data.utils;


import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

public class CustomGsonConverter extends GsonConverter{
    public static final String TAG = "CustomGsonConverter";
    public CustomGsonConverter(Gson gson) {
        super(gson);
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        String charset = "UTF-8";
        if (body.mimeType() != null) {
            charset = MimeUtil.parseCharset(body.mimeType());
        }
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(body.in(), charset);
            br = new BufferedReader(isr);
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                total.append(line);
            }

            String result = total.toString();

            if (result.substring(0,  1).equals("{")) {
                return new JSONObject(result);
            } else if (result.substring(0,  1).equals("[")) {
                JSONArray jsonArray = new JSONArray(result);
                boolean isJSONObjects = false;

                try {
                   if (jsonArray.length() > 0) {
                       jsonArray.getJSONObject(0);
                       isJSONObjects = true;
                   }
                } catch (JSONException jse) {
                    // ignore this scope
                }

                if (isJSONObjects) {
                    List<JSONObject> objects = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        objects.add(jsonArray.getJSONObject(i));
                    }

                    return objects;
                } else {
                    List<String> strings = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        strings.add(jsonArray.getString(i));
                    }

                    return strings;
                }
            } else {
                throw new ConversionException("response is not a JsonObject or JsonArray");
            }
        } catch (IOException e) {
            throw new ConversionException(e);
        } catch (JsonParseException e) {
            throw new ConversionException(e);
        } catch (JSONException e) {
            throw new ConversionException(e);
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException ignored) {
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    public TypedOutput toBody(Object object) {
        try {
            String jsonString = Constants.GSON_SENT.toJson(object);
//            if (BuildConfig.DEBUG) {
//                Log.d(TAG, "sent request: " + jsonString);
//            }
            return new JsonTypedOutput(jsonString.getBytes("UTF-8"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    private static class JsonTypedOutput implements TypedOutput {
        private final byte[] jsonBytes;
        private final String mimeType;

        JsonTypedOutput(byte[] jsonBytes, String encode) {
            this.jsonBytes = jsonBytes;
            this.mimeType = "application/json; charset=" + encode;
        }

        @Override
        public String fileName() {
            return null;
        }

        @Override
        public String mimeType() {
            return mimeType;
        }

        @Override
        public long length() {
            return jsonBytes.length;
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            out.write(jsonBytes);
        }
    }
}
