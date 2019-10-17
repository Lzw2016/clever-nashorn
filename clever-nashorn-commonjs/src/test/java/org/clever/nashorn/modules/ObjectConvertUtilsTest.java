package org.clever.nashorn.modules;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.DateTimeUtils;
import org.clever.nashorn.utils.ObjectConvertUtils;
import org.clever.nashorn.utils.ScriptEngineUtils;
import org.clever.nashorn.utils.StrFormatter;
import org.junit.Test;

import java.util.*;

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

    @Test
    public void t3() {
        // 这个实现有循环依赖问题
        Map<String, Object> mapA = new HashMap<>();
        Map<String, Object> mapB = new HashMap<>();
        mapA.put("A111", "a");
        mapA.put("A222", mapB);
        mapB.put("B111", "a");
        mapB.put("B222", mapA);
//        List<Object> set = new ArrayList<>(8);
//        set.add(mapA);
        log.info("-------------------------> {}", System.identityHashCode(mapA));
        Object object = ObjectConvertUtils.Instance.javaToJSObject(mapA);
        log.info("-------------------------> {}", StrFormatter.toString(object));
        object = ObjectConvertUtils.Instance.javaToJSObject(mapA);
        log.info("-------------------------> {}", StrFormatter.toString(object));

    }

    @Test
    public void t5() {
        ScriptObjectMirror scriptObjectMirror = ScriptEngineUtils.newDate(new Date());
        Object object = ObjectConvertUtils.jsBaseToJava(scriptObjectMirror);
        log.info("-------------------------> {}", DateTimeUtils.formatToString((Date) object));
    }

    @Test
    public void t6() {
        Map<String, Object> mapA = new HashMap<>();
        Map<String, Object> mapB = new HashMap<>();
        mapA.put("amount_limit", "a");
        mapA.put("coupon_require", mapB);
        mapB.put("get_time_start", "a");
        mapB.put("send_each_user", mapA);
        // underlineToCamel
        Object object = ObjectConvertUtils.Instance.underlineToCamel(mapA);
        log.info("-------------------------> {}", object);
    }

    @Test
    public void t7() {
        List<Object> list = new ArrayList<>();
        Map<String, Object> mapA = new HashMap<>();
        mapA.put("amount_limit", "a");
        mapA.put("coupon_require", "b");
        Map<String, Object> mapB = new HashMap<>();
        mapB.put("get_time_start", "a");
        mapB.put("send_each_user", "b");
        list.add(mapA);
        list.add(mapB);
        list.add(new HashMap<String, Object>() {{
            put("send_each", mapA);
        }});
        // underlineToCamel
        Object object = ObjectConvertUtils.Instance.underlineToCamel(list);
        log.info("-------------------------> {}", object);
    }
}
