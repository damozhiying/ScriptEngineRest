package ua.soft.sergii.util;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class JsonParseUtil {

    public static String getJsonString(Object o, Class<?> jsonView) throws IOException {
        return new ObjectMapper().writerWithView(jsonView).writeValueAsString(o);
    }

}
