package org.clever.nashorn.modules;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.Getter;
import org.clever.common.utils.mapper.JacksonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Console {

    @Getter
    private final String name;
    private final Logger log;

    public Console(String name) {
        this.name = name;
        this.log = LoggerFactory.getLogger(name);
    }

    public void log(Object... args) {
        if (args == null) {
            log.info("");
            return;
        }
        String format;
        if (args.length >= 1 && args[0] instanceof String) {
            format = (String) args[0];
        } else {
            format = "{}";
        }
        List<String> list = null;

        for (int index = 0; index < args.length; index++) {
            if (index <= 0) {
                continue;
            }
            if (list == null) {
                list = new ArrayList<>(args.length - 1);
            }
            String str = toStr(args[index]);
            list.add(str);
        }
        if (list == null) {
            log.info(format);
        } else {
            log.info(format, list.toArray());
        }
    }

    private String toStr(Object object) {
        if (object == null) {
            return null;
        }
        String str;
        if (object instanceof Byte
                || object instanceof Short
                || object instanceof Integer
                || object instanceof Long
                || object instanceof Float
                || object instanceof Double
                || object instanceof BigInteger
                || object instanceof BigDecimal
                || object instanceof Boolean
                || object instanceof String) {
            str = String.valueOf(object);
        } else if (object instanceof ScriptObjectMirror) {
            ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) object;
            if (scriptObjectMirror.isArray()) {
                str = JacksonMapper.nonEmptyMapper().toJson(scriptObjectMirror.values());
            } else if (scriptObjectMirror.isExtensible() || scriptObjectMirror.isFunction() || scriptObjectMirror.isStrictFunction()) {
                str = scriptObjectMirror.toString();
            } else {
                str = JacksonMapper.nonEmptyMapper().toJson(object);
            }
        } else {
            str = JacksonMapper.nonEmptyMapper().toJson(object);
        }
        return str;
    }
}
