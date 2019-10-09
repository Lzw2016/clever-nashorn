package org.clever.nashorn.internal;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.DateTimeUtils;
import org.clever.nashorn.utils.ObjectConvertUtils;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-10-06 22:01 <br/>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@Slf4j
public class RedisExecutor {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisExecutor(RedisTemplate<String, Object> redisTemplate) {
        if (redisTemplate == null) {
            throw new IllegalArgumentException("redisTemplate 不能为 null");
        }
        this.redisTemplate = redisTemplate;
    }

    // --------------------------------------------------------------------------------------------
    // Key 操作
    // --------------------------------------------------------------------------------------------

    /**
     * 删除 key
     *
     * @param key key
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除 key
     *
     * @param keys keys
     */
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 删除 key
     *
     * @param keys keys
     */
    public Long delete(String... keys) {
        return redisTemplate.delete(Arrays.asList(keys));
    }

    /**
     * 删除 key
     *
     * @param scriptObjectMirror keys
     */
    public Long delete(ScriptObjectMirror scriptObjectMirror) {
        if (!scriptObjectMirror.isArray()) {
            throw new IllegalArgumentException("参数必须是一个数组");
        }
        if (scriptObjectMirror.size() <= 0) {
            return 0L;
        }
        List<String> keys = new ArrayList<>(scriptObjectMirror.size());
        scriptObjectMirror.values().forEach(str -> {
            if (!(str instanceof String)) {
                throw new IllegalArgumentException("数组元素必须是字符串");
            }
            keys.add((String) str);
        });
        return redisTemplate.delete(keys);
    }

    /**
     * 序列化给定 key ，并返回被序列化的值
     *
     * @param key key
     */
    public byte[] dump(String key) {
        return redisTemplate.dump(key);
    }

    /**
     * 检查给定 key 是否存在
     *
     * @param key key
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 为给定 key 设置过期时间，以毫秒计
     *
     * @param key     key
     * @param timeout timeout以毫秒计
     */
    public Boolean expire(String key, long timeout) {
        return redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 为给定 key 设置过期时间，以毫秒计
     *
     * @param key     key
     * @param timeout timeout以毫秒计
     */
    public Boolean expire(String key, Double timeout) {
        return redisTemplate.expire(key, timeout.longValue(), TimeUnit.MILLISECONDS);
    }

    /**
     * 为给定 key 设置过期时间，以毫秒计
     *
     * @param key     key
     * @param timeout timeout以毫秒计
     */
    public Boolean expire(String key, Integer timeout) {
        return redisTemplate.expire(key, timeout.longValue(), TimeUnit.MILLISECONDS);
    }

    /**
     * 为给定 key 设置过期时间
     *
     * @param key  key
     * @param date 过期时间
     */
    public Boolean expireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    /**
     * 为给定 key 设置过期时间
     *
     * @param key                key
     * @param scriptObjectMirror 过期时间
     */
    public Boolean expireAt(String key, ScriptObjectMirror scriptObjectMirror) {
        Object date = ObjectConvertUtils.Instance.jsBaseToJava(scriptObjectMirror);
        if (!(date instanceof Date)) {
            throw new IllegalArgumentException("过期时间必须是一个Date");
        }
        return redisTemplate.expireAt(key, (Date) date);
    }

    /**
     * 为给定 key 设置过期时间
     *
     * @param key     key
     * @param dateStr 过期时间
     */
    public Boolean expireAt(String key, String dateStr) {
        Date date = DateTimeUtils.parseDate(dateStr);
        if (date == null) {
            throw new IllegalArgumentException("过期时间必须是一个时间字符串");
        }
        return redisTemplate.expireAt(key, date);
    }

    /**
     * 查找所有符合给定模式( pattern)的 key
     *
     * @param pattern 模式( pattern)
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 将当前数据库的 key 移动到给定的数据库 db 当中
     *
     * @param key     key
     * @param dbIndex dbIndex
     */
    public Boolean move(String key, int dbIndex) {
        return redisTemplate.move(key, dbIndex);
    }

    /**
     * 移除 key 的过期时间，key 将持久保持
     *
     * @param key key
     */
    public Boolean persist(String key) {
        return redisTemplate.persist(key);
    }

    /**
     * 以毫秒为单位返回 key 的剩余的过期时间
     *
     * @param key key
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
    }

    /**
     * 从当前数据库中随机返回一个 key
     */
    public String randomKey() {
        return redisTemplate.randomKey();
    }

    /**
     * 修改 key 的名称
     *
     * @param oldKey oldKey
     * @param newKey newKey
     */
    public void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 仅当 newkey 不存在时，将 key 改名为 newkey
     *
     * @param oldKey oldKey
     * @param newKey newKey
     */
    public Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 返回 key 所储存的值的类型
     *
     * @param key key
     */
    public DataType type(String key) {
        return redisTemplate.type(key);
    }


    // --------------------------------------------------------------------------------------------
    // String 操作
    // --------------------------------------------------------------------------------------------

    public void tt(String key, Object value) {
        redisTemplate.boundValueOps(key).set(value);
//        redisTemplate.type();
//        redisTemplate.delete()
//        redisTemplate.opsForValue().
    }

    // --------------------------------------------------------------------------------------------
    // Hash 操作
    // --------------------------------------------------------------------------------------------


    // --------------------------------------------------------------------------------------------
    // List 操作
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // Set 操作
    // --------------------------------------------------------------------------------------------


    // --------------------------------------------------------------------------------------------
    // Sorted Set 操作
    // --------------------------------------------------------------------------------------------


    // --------------------------------------------------------------------------------------------
    // List 操作
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // List 操作
    // --------------------------------------------------------------------------------------------
}
