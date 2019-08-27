package org.clever.nashorn.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.PropertyAccess;
import org.apache.commons.lang3.StringUtils;
import org.clever.nashorn.JSTools;

import javax.script.Bindings;
import java.io.IOException;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/26 17:47 <br/>
 */
public class BindingsJsonSerializer extends JsonSerializer<Object> {

    public final static BindingsJsonSerializer instance = new BindingsJsonSerializer();

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        String json = null;
        if (value instanceof JSObject) {
            json = JSTools.inspect((JSObject) value);
        } else if (value instanceof Bindings) {
            json = JSTools.inspect((Bindings) value);
        } else if (value instanceof PropertyAccess) {
            json = JSTools.inspect((PropertyAccess) value);
        }
        if (StringUtils.isNotBlank(json)) {
            gen.writeRawValue(json);
            // gen.writeObject(new JSONObject(json).toMap());
        }
    }
}
