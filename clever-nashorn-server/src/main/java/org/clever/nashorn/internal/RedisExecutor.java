package org.clever.nashorn.internal;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.DateTimeUtils;
import org.clever.nashorn.utils.ObjectConvertUtils;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.time.Duration;
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

    /**
     * 设置指定 key 的值
     *
     * @param key   key
     * @param value value
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 将值 value 关联到 key ，并将 key 的过期时间设为 seconds (以毫秒为单位)
     *
     * @param key     key
     * @param value   value
     * @param timeout 过期时间毫秒
     */
    public void set(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMillis(timeout));
    }

    /**
     * 将值 value 关联到 key ，并将 key 的过期时间设为 seconds (以毫秒为单位)
     *
     * @param key     key
     * @param value   value
     * @param timeout 过期时间毫秒
     */
    public void set(String key, Object value, Double timeout) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMillis(timeout.longValue()));
    }

    /**
     * 将值 value 关联到 key ，并将 key 的过期时间设为 seconds (以毫秒为单位)
     *
     * @param key     key
     * @param value   value
     * @param timeout 过期时间毫秒
     */
    public void set(String key, Object value, Integer timeout) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMillis(timeout.longValue()));
    }

    /**
     * 只有在 key 不存在时设置 key 的值
     *
     * @param key   key
     * @param value value
     */
    public Boolean setIfAbsent(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * @param key     key
     * @param value   value
     * @param timeout 过期时间毫秒
     */
    public Boolean setIfAbsent(String key, Object value, long timeout) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(timeout));
    }

    /**
     * @param key     key
     * @param value   value
     * @param timeout 过期时间毫秒
     */
    public Boolean setIfAbsent(String key, Object value, Double timeout) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(timeout.longValue()));
    }

    /**
     * @param key     key
     * @param value   value
     * @param timeout 过期时间毫秒
     */
    public Boolean setIfAbsent(String key, Object value, Integer timeout) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(timeout.longValue()));
    }


    /**
     * 返回 key 中字符串值的子字符
     *
     * @param key   key
     * @param start start
     * @param end   end
     */
    public String get(String key, long start, long end) {
        return redisTemplate.opsForValue().get(key, start, end);
    }

    /**
     * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)
     *
     * @param key   key
     * @param value value
     */
    public Object getAndSet(String key, Object value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * 对 key 所储存的字符串值，获取指定偏移量上的位(bit)
     *
     * @param key    key
     * @param offset 偏移量
     */
    public Boolean getBit(String key, long offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * 获取所有(一个或多个)给定 key 的值
     *
     * @param keys keys
     */
    public List<Object> multiGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 获取所有(一个或多个)给定 key 的值
     *
     * @param keys keys
     */
    public List<Object> multiGet(String... keys) {
        return redisTemplate.opsForValue().multiGet(Arrays.asList(keys));
    }

    /**
     * 获取所有(一个或多个)给定 key 的值
     *
     * @param scriptObjectMirror keys
     */
    public List<Object> multiGet(ScriptObjectMirror scriptObjectMirror) {
        if (!scriptObjectMirror.isArray()) {
            throw new IllegalArgumentException("参数必须是一个数组");
        }
        if (scriptObjectMirror.size() <= 0) {
            return Collections.emptyList();
        }
        List<String> keys = new ArrayList<>(scriptObjectMirror.size());
        scriptObjectMirror.values().forEach(str -> {
            if (!(str instanceof String)) {
                throw new IllegalArgumentException("数组元素必须是字符串");
            }
            keys.add((String) str);
        });
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 对 key 所储存的字符串值，设置或清除指定偏移量上的位(bit)
     *
     * @param key    key
     * @param offset 偏移量
     * @param value  值
     */
    public Boolean setBit(String key, long offset, boolean value) {
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * 用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始
     *
     * @param key    key
     * @param value  value
     * @param offset 偏移量
     */
    public void setRange(String key, Object value, long offset) {
        redisTemplate.opsForValue().set(key, value, offset);
    }

    /**
     * 返回 key 所储存的字符串值的长度
     *
     * @param key key
     */
    public Long size(String key) {
        return redisTemplate.opsForValue().size(key);
    }

    /**
     * 同时设置一个或多个 key-value 对
     *
     * @param map 多个 key-value 对
     */
    public void multiSet(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    /**
     * 同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在
     *
     * @param map 多个 key-value 对
     */
    public void multiSetIfAbsent(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSetIfAbsent(map);
    }

    /**
     * 将 key 中储存的数字值增 1
     *
     * @param key key
     */
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 将 key 所储存的值加上给定的增量值（increment）
     *
     * @param key   key
     * @param delta 增量值
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 将 key 所储存的值加上给定的增量值（increment）
     *
     * @param key   key
     * @param delta 增量值
     */
    public Long increment(String key, Integer delta) {
        return redisTemplate.opsForValue().increment(key, delta.longValue());
    }

    /**
     * 将 key 所储存的值加上给定的增量值（increment）
     *
     * @param key   key
     * @param delta 增量值
     */
    public Double increment(String key, double delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 将 key 中储存的数字值减 1
     *
     * @param key key
     */
    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    /**
     * key 所储存的值减去给定的减量值（decrement）
     *
     * @param key   key
     * @param delta 减量值
     */
    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * key 所储存的值减去给定的减量值（decrement）
     *
     * @param key   key
     * @param delta 减量值
     */
    public Long decrement(String key, Integer delta) {
        return redisTemplate.opsForValue().decrement(key, delta.longValue());
    }

    /**
     * key 所储存的值减去给定的减量值（decrement）
     *
     * @param key   key
     * @param delta 增量值
     */
    public Long decrement(String key, Double delta) {
        return redisTemplate.opsForValue().decrement(key, delta.longValue());
    }

    /**
     * 如果 key 已经存在并且是一个字符串， APPEND 命令将指定的 value 追加到该 key 原来值（value）的末尾
     *
     * @param key   key
     * @param value value
     */
    public Integer append(String key, String value) {
        return redisTemplate.opsForValue().append(key, value);
    }

    // --------------------------------------------------------------------------------------------
    // Hash 操作
    // --------------------------------------------------------------------------------------------

    /**
     * 删除一个或多个哈希表字段
     *
     * @param key      key
     * @param hashKeys hashKeys
     */
    public Long hDelete(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 删除一个或多个哈希表字段
     *
     * @param key      key
     * @param hashKeys hashKeys
     */
    public Long hDelete(String key, Collection<Object> hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys.toArray());
    }

    /**
     * 删除一个或多个哈希表字段
     *
     * @param key                key
     * @param scriptObjectMirror hashKeys
     */
    public Long hDelete(String key, ScriptObjectMirror scriptObjectMirror) {
        if (!scriptObjectMirror.isArray()) {
            throw new IllegalArgumentException("参数必须是一个数组");
        }
        if (scriptObjectMirror.size() <= 0) {
            return 0L;
        }
        return redisTemplate.opsForHash().delete(key, scriptObjectMirror.values().toArray());
    }

    /**
     * 查看哈希表 key 中，指定的字段是否存在
     *
     * @param key     key
     * @param hashKey hashKey
     */
    public Boolean hHasKey(String key, Object hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 获取存储在哈希表中指定字段的值
     *
     * @param key     key
     * @param hashKey hashKey
     */
    public Object hGet(String key, Object hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取所有给定字段的值
     *
     * @param key      key
     * @param hashKeys hashKeys
     */
    public List<Object> hMultiGet(String key, Collection<Object> hashKeys) {
        return redisTemplate.opsForHash().multiGet(key, hashKeys);
    }

    /**
     * 获取所有给定字段的值
     *
     * @param key      key
     * @param hashKeys hashKeys
     */
    public List<Object> hMultiGet(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().multiGet(key, Arrays.asList(hashKeys));
    }

    /**
     * 获取所有给定字段的值
     *
     * @param key                key
     * @param scriptObjectMirror hashKeys
     */
    public List<Object> hMultiGet(String key, ScriptObjectMirror scriptObjectMirror) {
        if (!scriptObjectMirror.isArray()) {
            throw new IllegalArgumentException("参数必须是一个数组");
        }
        if (scriptObjectMirror.size() <= 0) {
            return Collections.emptyList();
        }
        return redisTemplate.opsForHash().multiGet(key, scriptObjectMirror.values());
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     *
     * @param key     key
     * @param hashKey hashKey
     * @param delta   增量
     */
    public Long hIncrement(String key, Object hashKey, long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     *
     * @param key     key
     * @param hashKey hashKey
     * @param delta   增量
     */
    public Long hIncrement(String key, Object hashKey, Integer delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta.longValue());
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     *
     * @param key     key
     * @param hashKey hashKey
     * @param delta   增量
     */
    public Double hIncrement(String key, Object hashKey, double delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * 获取所有哈希表中的字段
     *
     * @param key key
     */
    public Set<Object> hKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 返回与hashKey关联的值的长度。如果键或hashKey不存在，则返回0
     *
     * @param key     key
     * @param hashKey hashKey
     */
    public Long hLengthOfValue(String key, Object hashKey) {
        return redisTemplate.opsForHash().lengthOfValue(key, hashKey);
    }

    /**
     * 获取哈希表中字段的数量
     *
     * @param key key
     */
    public Long hSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 同时将多个 field-value (域-值)对设置到哈希表 key 中
     *
     * @param key key
     * @param m   field-value
     */
    public void hPutAll(String key, Map<Object, Object> m) {
        redisTemplate.opsForHash().putAll(key, m);
    }

    /**
     * 将哈希表 key 中的字段 field 的值设为 value
     *
     * @param key     key
     * @param hashKey field
     * @param value   value
     */
    public void hPut(String key, Object hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 只有在字段 field 不存在时，设置哈希表字段的值
     *
     * @param key     key
     * @param hashKey field
     * @param value   字段的值
     */
    public Boolean hPutIfAbsent(String key, Object hashKey, Object value) {
        return redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /**
     * 获取哈希表中所有值
     *
     * @param key key
     */
    public List<Object> hValues(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    /**
     * 将整个散列存储在键上
     *
     * @param key key
     */
    public Map<Object, Object> hEntries(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 迭代哈希表中的键值对
     *
     * @param key      key
     * @param count    数量
     * @param pattern  字段匹配字符串
     * @param callback 回调函数
     */
    public void hScan(String key, long count, String pattern, ScriptObjectMirror callback) {
        ScanOptions scanOptions = ScanOptions.scanOptions().count(count).match(pattern).build();
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(key, scanOptions);
        while (cursor.hasNext()) {
            Map.Entry<Object, Object> entry = cursor.next();
            // TODO callback
        }
    }

    // --------------------------------------------------------------------------------------------
    // List 操作
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // Set 操作
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // Sorted Set 操作
    // --------------------------------------------------------------------------------------------


    public void tt(String key, Object value) {
        redisTemplate.boundValueOps(key).set(value);
//        redisTemplate.type();
//        redisTemplate.delete()


    }
}
