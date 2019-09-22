package org.clever.nashorn.modules;

import lombok.extern.slf4j.Slf4j;
import org.clever.nashorn.utils.ObjectConvertUtils;
import org.clever.nashorn.utils.StrFormatter;
import org.junit.Test;

import java.util.Date;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-09-22 08:30 <br/>
 */
@Slf4j
public class ObjectConvertUtilsTest {

    @Test
    public void t1() {
        Object date = ObjectConvertUtils.Instance.javaToJSObject(new Date());
        log.info("-------------------------> {}", StrFormatter.toString(date));
    }

    @Test
    public void t2() {
        Object array = ObjectConvertUtils.Instance.javaToJSObject(new Object[]{"a123", 12, 66.88, true, 'A'});
        log.info("-------------------------> {}", StrFormatter.toString(array));
    }
}
