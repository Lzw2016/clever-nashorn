package org.clever.nashorn.internal;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.Getter;
import org.clever.common.utils.mapper.JacksonMapper;
import org.clever.nashorn.utils.ScriptEngineUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Console {

    @Getter
    private final String filePath;
    @Getter
    private final String fileName;
    private final Logger log;

    public Console(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.log = LoggerFactory.getLogger(fileName);
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
            if (scriptObjectMirror.isFunction() || scriptObjectMirror.isStrictFunction()) {
                str = scriptObjectMirror.toString();
            } else {
                str = ScriptEngineUtils.stringify(scriptObjectMirror);
            }
        } else {
            str = JacksonMapper.nonEmptyMapper().toJson(object);
        }
        return str;
    }
}
