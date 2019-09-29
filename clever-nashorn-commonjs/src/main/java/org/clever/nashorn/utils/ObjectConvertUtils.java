package org.clever.nashorn.utils;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.exception.ExceptionUtils;
import org.clever.common.utils.mapper.BeanMapConverter;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.*;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/29 14:05 <br/>
 */
@Slf4j
public class ObjectConvertUtils {

    /**
     * 递归深度 32；集合大小 10000
     */
    public static final ObjectConvertUtils Instance = new ObjectConvertUtils(32, 1001);

    private static final NumberFormat Number_Format;

    static {
        Number_Format = NumberFormat.getInstance();
        Number_Format.setGroupingUsed(false);
    }

    /**
     * 递归深度
     */
    private final int recursiveMaxDeep;
    /**
     * 集合大小
     */
    private final int collectionMaxSize;
    /**
     * 当前递归深度-线程槽
     */
    private final ThreadLocal<Integer> deepSlot = new ThreadLocal<>();
    /**
     * 递归最大深度-线程槽
     */
    private final ThreadLocal<Integer> deepMaxSlot = new ThreadLocal<>();
    /**
     * 已经转换了的对象缓存 - 解决循环依赖问题
     */
    private final ThreadLocal<List<Object>> cacheSlot = new ThreadLocal<>();

    public ObjectConvertUtils(int recursiveMaxDeep, int collectionMaxSize) {
        this.recursiveMaxDeep = recursiveMaxDeep;
        this.collectionMaxSize = collectionMaxSize;
    }

    /**
     * Java对象转换成JS对象(慎用: 性能较差)
     */
    public Object javaToJSObject(Object obj) {
        if (obj == null) {
            return null;
        }
        final long startTime = System.currentTimeMillis();
        cacheSlot.set(new ArrayList<>(512));
        deepSlot.set(0);
        deepMaxSlot.set(0);
        Object res;
        try {
            res = doJavaToJSObject(obj);
        } catch (Throwable e) {
            throw ExceptionUtils.unchecked(e);
        } finally {
            log.debug("[Java对象转换成JS对象] 递归深度：{} | 耗时：{}ms", deepMaxSlot.get(), System.currentTimeMillis() - startTime);
            cacheSlot.remove();
            deepSlot.remove();
            deepMaxSlot.remove();
        }
        return res;
    }

    private Object doJavaToJSObject(Object obj) {
        if (obj != null) {
            for (Object cache : cacheSlot.get()) {
                if (cache == obj) {
                    return "Cycle Reference";
                }
            }
            cacheSlot.get().add(obj);
        }
        Object result;
        // 递归深度加 1
        Integer deep = deepSlot.get();
        if (deep == null) {
            deep = 0;
        }
        deep++;
        deepSlot.set(deep);
        Integer maxDeep = deepMaxSlot.get();
        if (maxDeep == null) {
            maxDeep = 0;
        }
        if (maxDeep < deep) {
            deepMaxSlot.set(deep);
        }
        // 超过了最大递归深度
        if (deep >= recursiveMaxDeep) {
            result = obj;
        } else if (obj instanceof ScriptObjectMirror) {
            // TODO 需要当Map处理？
            result = obj;
        } else if (obj == null) {// ------------------------------------------------------------------- null
            result = null;
        } else if (obj instanceof Byte) {// ----------------------------------------------------------- Number
            Byte val = (Byte) obj;
            result = val.intValue();
        } else if (obj instanceof Short) {
            Short val = (Short) obj;
            result = val.intValue();
        } else if (obj instanceof Integer) {
            result = obj;
        } else if (obj instanceof Long) {
            Long raw = (Long) obj;
            // js number 最大值 9007199254740992
            if (raw > 9007199254740992L) {
                result = Number_Format.format(raw);
            } else {
                result = raw.doubleValue();
            }
        } else if (obj instanceof Float) {
            Float val = (Float) obj;
            result = val.doubleValue();
        } else if (obj instanceof Double) {
            result = obj;
        } else if (obj instanceof BigInteger) {
            BigInteger bigInteger = (BigInteger) obj;
            result = bigInteger.doubleValue();
        } else if (obj instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) obj;
            result = bigDecimal.doubleValue();
        } else if (obj instanceof Boolean) {// -------------------------------------------------------- Boolean
            result = obj;
        } else if (obj instanceof String) {// --------------------------------------------------------- String
            result = obj;
        } else if (obj instanceof Character) {
            result = obj.toString();
        } else if (obj instanceof CharSequence) {
            result = obj.toString();
        } else if (obj instanceof Date) {// ----------------------------------------------------------- Date
            double val = (double) ((Date) obj).getTime();
            result = ScriptEngineUtils.newDate(val);
        } else if (obj instanceof DateTime) {
            double val = (double) ((DateTime) obj).toDate().getTime();
            result = ScriptEngineUtils.newDate(val);
        } else if (obj.getClass().isArray()) {// ------------------------------------------------------ Array
            ScriptObjectMirror nativeArray;
            Object[] array = (Object[]) obj;
            if (array.length < collectionMaxSize) {
                List<Object> list = new ArrayList<>(array.length);
                for (Object o : array) {
                    list.add(doJavaToJSObject(o));
                }
                nativeArray = ScriptEngineUtils.newArray(list);
            } else {
                nativeArray = ScriptEngineUtils.newArray(array);
            }
            // result = ScriptEngineUtils.newObject(nativeArray);
            result = nativeArray;
        } else if (obj instanceof Collection) {
            ScriptObjectMirror nativeArray;
            Collection collection = (Collection) obj;
            if (collection.size() < collectionMaxSize) {
                List<Object> list = new ArrayList<>(collection.size());
                for (Object o : collection) {
                    list.add(doJavaToJSObject(o));
                }
                nativeArray = ScriptEngineUtils.newArray(list);
            } else {
                nativeArray = ScriptEngineUtils.newArray(collection);
            }
            // result = ScriptEngineUtils.newObject(nativeArray);
            result = nativeArray;
        } else if (obj instanceof Map) {// ------------------------------------------------------------ Object
            Map<?, ?> map = (Map) obj;
            boolean flag = map.size() < collectionMaxSize;
            ScriptObjectMirror scriptObjectMirror = ScriptEngineUtils.newObject();
            int index = 0;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key;
                if (entry.getKey() instanceof String) {
                    key = (String) entry.getKey();
                } else if (entry.getKey() == null) {
                    key = String.valueOf(index);
                } else {
                    key = entry.getKey().getClass().getName() + "@" + Integer.toHexString(entry.getKey().hashCode());
                }
                scriptObjectMirror.put(key, flag ? doJavaToJSObject(entry.getValue()) : entry.getValue());
                index++;
            }
            result = scriptObjectMirror;
        } else {
            Map<String, Object> map = BeanMapConverter.toMap(obj);
            if (map.size() > 0) {
                ScriptObjectMirror scriptObjectMirror = ScriptEngineUtils.newObject();
                if (map.size() < collectionMaxSize) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        scriptObjectMirror.put(entry.getKey(), doJavaToJSObject(entry.getValue()));
                    }
                } else {
                    scriptObjectMirror.putAll(map);
                }
                result = scriptObjectMirror;
            } else {
                result = obj;
            }
        }
        deep--;
        deepSlot.set(deep);
        return result;
    }
}
