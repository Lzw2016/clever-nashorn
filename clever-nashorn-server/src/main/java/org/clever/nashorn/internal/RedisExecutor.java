package org.clever.nashorn.internal;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.DateTimeUtils;
import org.clever.nashorn.internal.utils.InternalUtils;
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
@SuppressWarnings({"WeakerAccess", "unused", "DuplicatedCode"})
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
    public Boolean kDelete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除 key
     *
     * @param keys keys
     */
    public Long kDelete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 删除 key
     *
     * @param keys keys
     */
    public Long kDelete(String... keys) {
        return redisTemplate.delete(Arrays.asList(keys));
    }

    /**
     * 删除 key
     *
     * @param scriptObjectMirror keys
     */
    public Long kDelete(ScriptObjectMirror scriptObjectMirror) {
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
    public byte[] kDump(String key) {
        return redisTemplate.dump(key);
    }

    /**
     * 检查给定 key 是否存在
     *
     * @param key key
     */
    public Boolean kHasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 为给定 key 设置过期时间，以毫秒计
     *
     * @param key     key
     * @param timeout timeout以毫秒计
     */
    public Boolean kExpire(String key, long timeout) {
        return redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 为给定 key 设置过期时间，以毫秒计
     *
     * @param key     key
     * @param timeout timeout以毫秒计
     */
    public Boolean kExpire(String key, Double timeout) {
        return redisTemplate.expire(key, timeout.longValue(), TimeUnit.MILLISECONDS);
    }

    /**
     * 为给定 key 设置过期时间，以毫秒计
     *
     * @param key     key
     * @param timeout timeout以毫秒计
     */
    public Boolean kExpire(String key, Integer timeout) {
        return redisTemplate.expire(key, timeout.longValue(), TimeUnit.MILLISECONDS);
    }

    /**
     * 为给定 key 设置过期时间
     *
     * @param key  key
     * @param date 过期时间
     */
    public Boolean kExpireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    /**
     * 为给定 key 设置过期时间
     *
     * @param key                key
     * @param scriptObjectMirror 过期时间
     */
    public Boolean kExpireAt(String key, ScriptObjectMirror scriptObjectMirror) {
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
    public Boolean kExpireAt(String key, String dateStr) {
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
    public Boolean kMove(String key, int dbIndex) {
        return redisTemplate.move(key, dbIndex);
    }

    /**
     * 移除 key 的过期时间，key 将持久保持
     *
     * @param key key
     */
    public Boolean kPersist(String key) {
        return redisTemplate.persist(key);
    }

    /**
     * 以毫秒为单位返回 key 的剩余的过期时间
     *
     * @param key key
     */
    public Long kGetExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
    }

    /**
     * 从当前数据库中随机返回一个 key
     */
    public String kRandomKey() {
        return redisTemplate.randomKey();
    }

    /**
     * 修改 key 的名称
     *
     * @param oldKey oldKey
     * @param newKey newKey
     */
    public void kRename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 仅当 newkey 不存在时，将 key 改名为 newkey
     *
     * @param oldKey oldKey
     * @param newKey newKey
     */
    public Boolean kRenameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 返回 key 所储存的值的类型
     *
     * @param key key
     */
    public DataType kType(String key) {
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
    public void vSet(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 将值 value 关联到 key ，并将 key 的过期时间设为 seconds (以毫秒为单位)
     *
     * @param key     key
     * @param value   value
     * @param timeout 过期时间毫秒
     */
    public void vSet(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMillis(timeout));
    }

    /**
     * 将值 value 关联到 key ，并将 key 的过期时间设为 seconds (以毫秒为单位)
     *
     * @param key     key
     * @param value   value
     * @param timeout 过期时间毫秒
     */
    public void vSet(String key, Object value, Double timeout) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMillis(timeout.longValue()));
    }

    /**
     * 将值 value 关联到 key ，并将 key 的过期时间设为 seconds (以毫秒为单位)
     *
     * @param key     key
     * @param value   value
     * @param timeout 过期时间毫秒
     */
    public void vSet(String key, Object value, Integer timeout) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMillis(timeout.longValue()));
    }

    /**
     * 只有在 key 不存在时设置 key 的值
     *
     * @param key   key
     * @param value value
     */
    public Boolean vSetIfAbsent(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * @param key     key
     * @param value   value
     * @param timeout 过期时间毫秒
     */
    public Boolean vSetIfAbsent(String key, Object value, long timeout) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(timeout));
    }

    /**
     * @param key     key
     * @param value   value
     * @param timeout 过期时间毫秒
     */
    public Boolean vSetIfAbsent(String key, Object value, Double timeout) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(timeout.longValue()));
    }

    /**
     * @param key     key
     * @param value   value
     * @param timeout 过期时间毫秒
     */
    public Boolean vSetIfAbsent(String key, Object value, Integer timeout) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(timeout.longValue()));
    }


    /**
     * 返回 key 中字符串值的子字符
     *
     * @param key   key
     * @param start start
     * @param end   end
     */
    public String vGet(String key, long start, long end) {
        return redisTemplate.opsForValue().get(key, start, end);
    }

    /**
     * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)
     *
     * @param key   key
     * @param value value
     */
    public Object vGetAndSet(String key, Object value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * 对 key 所储存的字符串值，获取指定偏移量上的位(bit)
     *
     * @param key    key
     * @param offset 偏移量
     */
    public Boolean vGetBit(String key, long offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * 获取所有(一个或多个)给定 key 的值
     *
     * @param keys keys
     */
    public List<Object> vMultiGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 获取所有(一个或多个)给定 key 的值
     *
     * @param keys keys
     */
    public List<Object> vMultiGet(String... keys) {
        return redisTemplate.opsForValue().multiGet(Arrays.asList(keys));
    }

    /**
     * 获取所有(一个或多个)给定 key 的值
     *
     * @param scriptObjectMirror keys
     */
    public List<Object> vMultiGet(ScriptObjectMirror scriptObjectMirror) {
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
    public Boolean vSetBit(String key, long offset, boolean value) {
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * 用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始
     *
     * @param key    key
     * @param value  value
     * @param offset 偏移量
     */
    public void vSetRange(String key, Object value, long offset) {
        redisTemplate.opsForValue().set(key, value, offset);
    }

    /**
     * 返回 key 所储存的字符串值的长度
     *
     * @param key key
     */
    public Long vSize(String key) {
        return redisTemplate.opsForValue().size(key);
    }

    /**
     * 同时设置一个或多个 key-value 对
     *
     * @param map 多个 key-value 对
     */
    public void vMultiSet(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    /**
     * 同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在
     *
     * @param map 多个 key-value 对
     */
    public void vMultiSetIfAbsent(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSetIfAbsent(map);
    }

    /**
     * 将 key 中储存的数字值增 1
     *
     * @param key key
     */
    public Long vIncrement(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 将 key 所储存的值加上给定的增量值（increment）
     *
     * @param key   key
     * @param delta 增量值
     */
    public Long vIncrement(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 将 key 所储存的值加上给定的增量值（increment）
     *
     * @param key   key
     * @param delta 增量值
     */
    public Long vIncrement(String key, Integer delta) {
        return redisTemplate.opsForValue().increment(key, delta.longValue());
    }

    /**
     * 将 key 所储存的值加上给定的增量值（increment）
     *
     * @param key   key
     * @param delta 增量值
     */
    public Double vIncrement(String key, double delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 将 key 中储存的数字值减 1
     *
     * @param key key
     */
    public Long vDecrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    /**
     * key 所储存的值减去给定的减量值（decrement）
     *
     * @param key   key
     * @param delta 减量值
     */
    public Long vDecrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * key 所储存的值减去给定的减量值（decrement）
     *
     * @param key   key
     * @param delta 减量值
     */
    public Long vDecrement(String key, Integer delta) {
        return redisTemplate.opsForValue().decrement(key, delta.longValue());
    }

    /**
     * key 所储存的值减去给定的减量值（decrement）
     *
     * @param key   key
     * @param delta 增量值
     */
    public Long vDecrement(String key, Double delta) {
        return redisTemplate.opsForValue().decrement(key, delta.longValue());
    }

    /**
     * 如果 key 已经存在并且是一个字符串， APPEND 命令将指定的 value 追加到该 key 原来值（value）的末尾
     *
     * @param key   key
     * @param value value
     */
    public Integer vAppend(String key, String value) {
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
     * @param key                key
     * @param count              数量
     * @param pattern            字段匹配字符串
     * @param scriptObjectMirror 回调函数
     */
    public void hScan(String key, long count, String pattern, ScriptObjectMirror scriptObjectMirror) {
        ScriptObjectMirror callback = InternalUtils.getCallback(scriptObjectMirror);
        ScanOptions scanOptions = ScanOptions.scanOptions().count(count).match(pattern).build();
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(key, scanOptions);
        while (cursor.hasNext()) {
            Map.Entry<Object, Object> entry = cursor.next();
            Object res = callback.call(entry, entry.getKey(), entry.getValue());
            if (res instanceof Boolean && (Boolean) res) {
                break;
            }
        }
    }

    /**
     * 迭代哈希表中的键值对
     *
     * @param key                key
     * @param count              数量
     * @param pattern            字段匹配字符串
     * @param scriptObjectMirror 回调函数
     */
    public void hScan(String key, Integer count, String pattern, ScriptObjectMirror scriptObjectMirror) {
        hScan(key, count.longValue(), pattern, scriptObjectMirror);
    }

    /**
     * 迭代哈希表中的键值对
     *
     * @param key                key
     * @param count              数量
     * @param pattern            字段匹配字符串
     * @param scriptObjectMirror 回调函数
     */
    public void hScan(String key, Double count, String pattern, ScriptObjectMirror scriptObjectMirror) {
        hScan(key, count.longValue(), pattern, scriptObjectMirror);
    }

    // --------------------------------------------------------------------------------------------
    // List 操作
    // --------------------------------------------------------------------------------------------

    /**
     * 获取列表指定范围内的元素
     *
     * @param key   key
     * @param start start
     * @param end   end
     */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取列表指定范围内的元素
     *
     * @param key   key
     * @param start start
     * @param end   end
     */
    public List<Object> lRange(String key, Integer start, Integer end) {
        return redisTemplate.opsForList().range(key, start.longValue(), end.longValue());
    }

    /**
     * 获取列表指定范围内的元素
     *
     * @param key   key
     * @param start start
     * @param end   end
     */
    public List<Object> lRange(String key, Double start, Double end) {
        return redisTemplate.opsForList().range(key, start.longValue(), end.longValue());
    }

    /**
     * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除
     *
     * @param key   key
     * @param start start
     * @param end   end
     */
    public void lTrim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    /**
     * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除
     *
     * @param key   key
     * @param start start
     * @param end   end
     */
    public void lTrim(String key, Integer start, Integer end) {
        redisTemplate.opsForList().trim(key, start.longValue(), end.longValue());
    }

    /**
     * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除
     *
     * @param key   key
     * @param start start
     * @param end   end
     */
    public void lTrim(String key, Double start, Double end) {
        redisTemplate.opsForList().trim(key, start.longValue(), end.longValue());
    }

    /**
     * 获取列表长度
     *
     * @param key key
     */
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 将一个或多个值插入到列表头部
     *
     * @param key   key
     * @param value value
     */
    public Long lLeftPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 将一个或多个值插入到列表头部
     *
     * @param key    key
     * @param values values
     */
    public Long lLeftPushAll(String key, Object... values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 将一个或多个值插入到列表头部
     *
     * @param key    key
     * @param values values
     */
    public Long lLeftPushAll(String key, Collection<Object> values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 将一个或多个值插入到列表头部
     *
     * @param key                key
     * @param scriptObjectMirror values
     */
    public Long lLeftPushAll(String key, ScriptObjectMirror scriptObjectMirror) {
        if (!scriptObjectMirror.isArray()) {
            throw new IllegalArgumentException("参数必须是一个数组");
        }
        if (scriptObjectMirror.size() <= 0) {
            return 0L;
        }
        return redisTemplate.opsForList().leftPushAll(key, scriptObjectMirror.values());
    }

    /**
     * 将一个值插入到已存在的列表头部
     *
     * @param key   key
     * @param value value
     */
    public Long lLeftPushIfPresent(String key, Object value) {
        return redisTemplate.opsForList().leftPushIfPresent(key, value);
    }

    /**
     * 将值前置到键值之前
     *
     * @param key   key
     * @param pivot pivot
     * @param value value
     */
    public Long lLeftPush(String key, Object pivot, Object value) {
        return redisTemplate.opsForList().leftPush(key, pivot, value);
    }

    /**
     * 在列表中添加一个或多个值
     *
     * @param key   key
     * @param value value
     */
    public Long lRightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 在列表中添加一个或多个值
     *
     * @param key    key
     * @param values value
     */
    public Long lRightPushAll(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 在列表中添加一个或多个值
     *
     * @param key    key
     * @param values value
     */
    public Long lRightPushAll(String key, Collection<Object> values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 在列表中添加一个或多个值
     *
     * @param key                key
     * @param scriptObjectMirror value
     */
    public Long lRightPushAll(String key, ScriptObjectMirror scriptObjectMirror) {
        if (!scriptObjectMirror.isArray()) {
            throw new IllegalArgumentException("参数必须是一个数组");
        }
        if (scriptObjectMirror.size() <= 0) {
            return 0L;
        }
        return redisTemplate.opsForList().rightPushAll(key, scriptObjectMirror.values());
    }

    /**
     * 仅当列表存在时才向键追加值
     *
     * @param key   key
     * @param value value
     */
    public Long lRightPushIfPresent(String key, Object value) {
        return redisTemplate.opsForList().rightPushIfPresent(key, value);
    }

    /**
     * 在键值之前追加值
     *
     * @param key   key
     * @param pivot pivot
     * @param value value
     */
    public Long lRightPush(String key, Object pivot, Object value) {
        return redisTemplate.opsForList().rightPush(key, pivot, value);
    }

    /**
     * 通过索引设置列表元素的值
     *
     * @param key   key
     * @param index 索引
     * @param value value
     */
    public void lSet(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 通过索引设置列表元素的值
     *
     * @param key   key
     * @param index 索引
     * @param value value
     */
    public void lSet(String key, Integer index, Object value) {
        redisTemplate.opsForList().set(key, index.longValue(), value);
    }

    /**
     * 通过索引设置列表元素的值
     *
     * @param key   key
     * @param index 索引
     * @param value value
     */
    public void lSet(String key, Double index, Object value) {
        redisTemplate.opsForList().set(key, index.longValue(), value);
    }

    /**
     * 移除列表元素，从存储在键上的列表中删除第一次出现的值计数
     *
     * @param key   key
     * @param count count
     * @param value value
     */
    public Long lRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    /**
     * 移除列表元素，从存储在键上的列表中删除第一次出现的值计数
     *
     * @param key   key
     * @param count count
     * @param value value
     */
    public Long lRemove(String key, Integer count, Object value) {
        return redisTemplate.opsForList().remove(key, count.longValue(), value);
    }

    /**
     * 移除列表元素，从存储在键上的列表中删除第一次出现的值计数
     *
     * @param key   key
     * @param count count
     * @param value value
     */
    public Long lRemove(String key, Double count, Object value) {
        return redisTemplate.opsForList().remove(key, count.longValue(), value);
    }

    /**
     * 通过索引获取列表中的元素
     *
     * @param key   key
     * @param index 索引
     */
    public Object lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 通过索引获取列表中的元素
     *
     * @param key   key
     * @param index 索引
     */
    public Object lIndex(String key, Integer index) {
        return redisTemplate.opsForList().index(key, index.longValue());
    }

    /**
     * 通过索引获取列表中的元素
     *
     * @param key   key
     * @param index 索引
     */
    public Object lIndex(String key, Double index) {
        return redisTemplate.opsForList().index(key, index.longValue());
    }

    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key key
     */
    public Object lLeftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key     key
     * @param timeout timeout 毫秒
     */
    public Object lLeftPop(String key, long timeout) {
        return redisTemplate.opsForList().leftPop(key, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key     key
     * @param timeout timeout 毫秒
     */
    public Object lLeftPop(String key, Integer timeout) {
        return redisTemplate.opsForList().leftPop(key, timeout.longValue(), TimeUnit.MILLISECONDS);
    }

    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key     key
     * @param timeout timeout 毫秒
     */
    public Object lLeftPop(String key, Double timeout) {
        return redisTemplate.opsForList().leftPop(key, timeout.longValue(), TimeUnit.MILLISECONDS);
    }

    /**
     * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key key
     */
    public Object lRightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key     key
     * @param timeout timeout 毫秒
     */
    public Object lRightPop(String key, long timeout) {
        return redisTemplate.opsForList().rightPop(key, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key     key
     * @param timeout timeout 毫秒
     */
    public Object lRightPop(String key, Integer timeout) {
        return redisTemplate.opsForList().rightPop(key, timeout.longValue(), TimeUnit.MILLISECONDS);
    }

    /**
     * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key     key
     * @param timeout timeout 毫秒
     */
    public Object lRightPop(String key, Double timeout) {
        return redisTemplate.opsForList().rightPop(key, timeout.longValue(), TimeUnit.MILLISECONDS);
    }

    /**
     * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param sourceKey      sourceKey
     * @param destinationKey destinationKey
     */
    public Object lRightPopAndLeftPush(String sourceKey, String destinationKey) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
    }

    /**
     * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param sourceKey      sourceKey
     * @param destinationKey destinationKey
     * @param timeout        timeout 毫秒
     */
    public Object lRightPopAndLeftPush(String sourceKey, String destinationKey, long timeout) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param sourceKey      sourceKey
     * @param destinationKey destinationKey
     * @param timeout        timeout 毫秒
     */
    public Object lRightPopAndLeftPush(String sourceKey, String destinationKey, Integer timeout) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey, timeout.longValue(), TimeUnit.MILLISECONDS);
    }

    /**
     * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param sourceKey      sourceKey
     * @param destinationKey destinationKey
     * @param timeout        timeout 毫秒
     */
    public Object lRightPopAndLeftPush(String sourceKey, String destinationKey, Double timeout) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey, timeout.longValue(), TimeUnit.MILLISECONDS);
    }

    // --------------------------------------------------------------------------------------------
    // Set 操作
    // --------------------------------------------------------------------------------------------

    /**
     * 向集合添加一个或多个成员
     *
     * @param key    key
     * @param values values
     */
    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 向集合添加一个或多个成员
     *
     * @param key    key
     * @param values values
     */
    public Long sAdd(String key, Collection<Object> values) {
        return redisTemplate.opsForSet().add(key, values.toArray());
    }

    /**
     * 向集合添加一个或多个成员
     *
     * @param key                key
     * @param scriptObjectMirror values
     */
    public Long sAdd(String key, ScriptObjectMirror scriptObjectMirror) {
        if (!scriptObjectMirror.isArray()) {
            throw new IllegalArgumentException("参数必须是一个数组");
        }
        if (scriptObjectMirror.size() <= 0) {
            return 0L;
        }
        return redisTemplate.opsForSet().add(key, scriptObjectMirror.values().toArray());
    }

    /**
     * 移除集合中一个或多个成员
     *
     * @param key    key
     * @param values values
     */
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 移除集合中一个或多个成员
     *
     * @param key    key
     * @param values values
     */
    public Long sRemove(String key, Collection<Object> values) {
        return redisTemplate.opsForSet().remove(key, values.toArray());
    }

    /**
     * 移除集合中一个或多个成员
     *
     * @param key                key
     * @param scriptObjectMirror values
     */
    public Long sRemove(String key, ScriptObjectMirror scriptObjectMirror) {
        if (!scriptObjectMirror.isArray()) {
            throw new IllegalArgumentException("参数必须是一个数组");
        }
        if (scriptObjectMirror.size() <= 0) {
            return 0L;
        }
        return redisTemplate.opsForSet().remove(key, scriptObjectMirror.values().toArray());
    }

    /**
     * 移除并返回集合中的一个随机元素
     *
     * @param key key
     */
    public Object sPop(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    /**
     * 移除并返回集合中的count个随机元素
     *
     * @param key   key
     * @param count count
     */
    public List<Object> sPop(String key, long count) {
        return redisTemplate.opsForSet().pop(key, count);
    }

    /**
     * 移除并返回集合中的count个随机元素
     *
     * @param key   key
     * @param count count
     */
    public List<Object> sPop(String key, Integer count) {
        return redisTemplate.opsForSet().pop(key, count.longValue());
    }

    /**
     * 移除并返回集合中的count个随机元素
     *
     * @param key   key
     * @param count count
     */
    public List<Object> sPop(String key, Double count) {
        return redisTemplate.opsForSet().pop(key, count.longValue());
    }

    /**
     * 将 value 元素从 key 集合移动到 destKey 集合
     *
     * @param key     key
     * @param value   value
     * @param destKey destKey
     */
    public Boolean sMove(String key, Object value, String destKey) {
        return redisTemplate.opsForSet().move(key, value, destKey);
    }

    /**
     * 获取集合的成员数
     *
     * @param key key
     */
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 判断 member 元素是否是集合 key 的成员
     *
     * @param key    key
     * @param member member 元素
     */
    public Boolean sIsMember(String key, Object member) {
        return redisTemplate.opsForSet().isMember(key, member);
    }

    /**
     * 返回给定所有集合的交集
     *
     * @param key      key
     * @param otherKey otherKey
     */
    public Set<Object> sIntersect(String key, String otherKey) {
        return redisTemplate.opsForSet().intersect(key, otherKey);
    }

    /**
     * 返回给定所有集合的交集
     *
     * @param key       key
     * @param otherKeys otherKeys
     */
    public Set<Object> sIntersect(String key, Collection<String> otherKeys) {
        return redisTemplate.opsForSet().intersect(key, otherKeys);
    }

    /**
     * 返回给定所有集合的交集
     *
     * @param key       key
     * @param otherKeys otherKeys
     */
    public Set<Object> sIntersect(String key, String... otherKeys) {
        return redisTemplate.opsForSet().intersect(key, Arrays.asList(otherKeys));
    }

    /**
     * 返回给定所有集合的交集
     *
     * @param key                key
     * @param scriptObjectMirror otherKeys
     */
    public Set<Object> sIntersect(String key, ScriptObjectMirror scriptObjectMirror) {
        if (!scriptObjectMirror.isArray()) {
            throw new IllegalArgumentException("参数必须是一个数组");
        }
        if (scriptObjectMirror.size() <= 0) {
            return Collections.emptySet();
        }
        Collection<String> otherKeys = new HashSet<>(scriptObjectMirror.size());
        scriptObjectMirror.values().forEach(keyTmp -> {
            if (!(keyTmp instanceof String)) {
                throw new IllegalArgumentException("数组元素必须是字符串类型");
            }
            otherKeys.add((String) keyTmp);
        });
        return redisTemplate.opsForSet().intersect(key, otherKeys);
    }

    /**
     * 返回给定所有集合的交集并存储在 destination 中
     *
     * @param key      key
     * @param otherKey otherKey
     * @param destKey  destKey
     */
    public Long sIntersectAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(key, otherKey, destKey);
    }

    /**
     * 返回给定所有集合的交集并存储在 destination 中
     *
     * @param key       key
     * @param otherKeys otherKeys
     * @param destKey   destKey
     */
    public Long sIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(key, otherKeys, destKey);
    }

    /**
     * 返回给定所有集合的交集并存储在 destination 中
     *
     * @param key       key
     * @param otherKeys otherKeys
     * @param destKey   destKey
     */
    public Long sIntersectAndStore(String key, String[] otherKeys, String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(key, Arrays.asList(otherKeys), destKey);
    }

    /**
     * 返回给定所有集合的交集并存储在 destination 中
     *
     * @param key                key
     * @param scriptObjectMirror otherKeys
     * @param destKey            destKey
     */
    public Long sIntersectAndStore(String key, ScriptObjectMirror scriptObjectMirror, String destKey) {
        if (!scriptObjectMirror.isArray()) {
            throw new IllegalArgumentException("参数必须是一个数组");
        }
        if (scriptObjectMirror.size() <= 0) {
            return 0L;
        }
        Collection<String> otherKeys = new HashSet<>(scriptObjectMirror.size());
        scriptObjectMirror.values().forEach(keyTmp -> {
            if (!(keyTmp instanceof String)) {
                throw new IllegalArgumentException("数组元素必须是字符串类型");
            }
            otherKeys.add((String) keyTmp);
        });
        return redisTemplate.opsForSet().intersectAndStore(key, otherKeys, destKey);
    }

    /**
     * 返回所有给定集合的并集
     *
     * @param key      key
     * @param otherKey otherKey
     */
    public Set<Object> sUnion(String key, String otherKey) {
        return redisTemplate.opsForSet().union(key, otherKey);
    }

    /**
     * 返回所有给定集合的并集
     *
     * @param key       key
     * @param otherKeys otherKey
     */
    public Set<Object> sUnion(String key, Collection<String> otherKeys) {
        return redisTemplate.opsForSet().union(key, otherKeys);
    }

    /**
     * 返回所有给定集合的并集
     *
     * @param key       key
     * @param otherKeys otherKey
     */
    public Set<Object> sUnion(String key, String... otherKeys) {
        return redisTemplate.opsForSet().union(key, Arrays.asList(otherKeys));
    }

    /**
     * 返回所有给定集合的并集
     *
     * @param key                key
     * @param scriptObjectMirror otherKey
     */
    public Set<Object> sUnion(String key, ScriptObjectMirror scriptObjectMirror) {
        if (!scriptObjectMirror.isArray()) {
            throw new IllegalArgumentException("参数必须是一个数组");
        }
        if (scriptObjectMirror.size() <= 0) {
            return Collections.emptySet();
        }
        Collection<String> otherKeys = new HashSet<>(scriptObjectMirror.size());
        scriptObjectMirror.values().forEach(keyTmp -> {
            if (!(keyTmp instanceof String)) {
                throw new IllegalArgumentException("数组元素必须是字符串类型");
            }
            otherKeys.add((String) keyTmp);
        });
        return redisTemplate.opsForSet().union(key, otherKeys);
    }

    /**
     * 所有给定集合的并集存储在 destKey 集合中
     *
     * @param key      key
     * @param otherKey otherKey
     * @param destKey  destKey
     */
    public Long sUnionAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * 所有给定集合的并集存储在 destKey 集合中
     *
     * @param key       key
     * @param otherKeys otherKeys
     * @param destKey   destKey
     */
    public Long sUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key, otherKeys, destKey);
    }

    /**
     * 所有给定集合的并集存储在 destKey 集合中
     *
     * @param key       key
     * @param otherKeys otherKeys
     * @param destKey   destKey
     */
    public Long sUnionAndStore(String key, String[] otherKeys, String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key, Arrays.asList(otherKeys), destKey);
    }

    /**
     * 所有给定集合的并集存储在 destKey 集合中
     *
     * @param key                key
     * @param scriptObjectMirror otherKeys
     * @param destKey            destKey
     */
    public Long sUnionAndStore(String key, ScriptObjectMirror scriptObjectMirror, String destKey) {
        if (!scriptObjectMirror.isArray()) {
            throw new IllegalArgumentException("参数必须是一个数组");
        }
        if (scriptObjectMirror.size() <= 0) {
            return 0L;
        }
        Collection<String> otherKeys = new HashSet<>(scriptObjectMirror.size());
        scriptObjectMirror.values().forEach(keyTmp -> {
            if (!(keyTmp instanceof String)) {
                throw new IllegalArgumentException("数组元素必须是字符串类型");
            }
            otherKeys.add((String) keyTmp);
        });
        return redisTemplate.opsForSet().unionAndStore(key, otherKeys, destKey);
    }

    /**
     * 返回给定所有集合的差集
     *
     * @param key      key
     * @param otherKey otherKey
     */
    public Set<Object> sDifference(String key, String otherKey) {
        return redisTemplate.opsForSet().difference(key, otherKey);
    }

    /**
     * 返回给定所有集合的差集
     *
     * @param key      key
     * @param otherKey otherKey
     */
    public Set<Object> sDifference(String key, Collection<String> otherKey) {
        return redisTemplate.opsForSet().difference(key, otherKey);
    }

    /**
     * 返回给定所有集合的差集
     *
     * @param key      key
     * @param otherKey otherKey
     */
    public Set<Object> sDifference(String key, String... otherKey) {
        return redisTemplate.opsForSet().difference(key, Arrays.asList(otherKey));
    }

    /**
     * 返回给定所有集合的差集
     *
     * @param key                key
     * @param scriptObjectMirror otherKey
     */
    public Set<Object> sDifference(String key, ScriptObjectMirror scriptObjectMirror) {
        if (!scriptObjectMirror.isArray()) {
            throw new IllegalArgumentException("参数必须是一个数组");
        }
        if (scriptObjectMirror.size() <= 0) {
            return Collections.emptySet();
        }
        Collection<String> otherKeys = new HashSet<>(scriptObjectMirror.size());
        scriptObjectMirror.values().forEach(keyTmp -> {
            if (!(keyTmp instanceof String)) {
                throw new IllegalArgumentException("数组元素必须是字符串类型");
            }
            otherKeys.add((String) keyTmp);
        });
        return redisTemplate.opsForSet().difference(key, otherKeys);
    }

    /**
     * 返回给定所有集合的差集并存储在 destKey 中
     *
     * @param key      key
     * @param otherKey otherKey
     * @param destKey  destKey
     */
    public Long sDifferenceAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key, otherKey, destKey);
    }

    /**
     * 返回给定所有集合的差集并存储在 destKey 中
     *
     * @param key       key
     * @param otherKeys otherKeys
     * @param destKey   destKey
     */
    public Long sDifferenceAndStore(String key, Collection<String> otherKeys, String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key, otherKeys, destKey);
    }

    /**
     * 返回给定所有集合的差集并存储在 destKey 中
     *
     * @param key       key
     * @param otherKeys otherKeys
     * @param destKey   destKey
     */
    public Long sDifferenceAndStore(String key, String[] otherKeys, String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key, Arrays.asList(otherKeys), destKey);
    }

    /**
     * 返回给定所有集合的差集并存储在 destKey 中
     *
     * @param key                key
     * @param scriptObjectMirror otherKeys
     * @param destKey            destKey
     */
    public Long sDifferenceAndStore(String key, ScriptObjectMirror scriptObjectMirror, String destKey) {
        if (!scriptObjectMirror.isArray()) {
            throw new IllegalArgumentException("参数必须是一个数组");
        }
        if (scriptObjectMirror.size() <= 0) {
            return 0L;
        }
        Collection<String> otherKeys = new HashSet<>(scriptObjectMirror.size());
        scriptObjectMirror.values().forEach(keyTmp -> {
            if (!(keyTmp instanceof String)) {
                throw new IllegalArgumentException("数组元素必须是字符串类型");
            }
            otherKeys.add((String) keyTmp);
        });
        return redisTemplate.opsForSet().differenceAndStore(key, otherKeys, destKey);
    }

    /**
     * 返回集合中的所有成员
     *
     * @param key key
     */
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 返回集合中一个或多个随机数
     *
     * @param key key
     */
    public Object sRandomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }

    /**
     * 从集合中获取不同的随机元素
     *
     * @param key   key
     * @param count 数量
     */
    public Set<Object> sDistinctRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().distinctRandomMembers(key, count);
    }

    /**
     * 从集合中获取不同的随机元素
     *
     * @param key   key
     * @param count 数量
     */
    public Set<Object> sDistinctRandomMembers(String key, Integer count) {
        return redisTemplate.opsForSet().distinctRandomMembers(key, count.longValue());
    }

    /**
     * 从集合中获取不同的随机元素
     *
     * @param key   key
     * @param count 数量
     */
    public Set<Object> sDistinctRandomMembers(String key, Double count) {
        return redisTemplate.opsForSet().distinctRandomMembers(key, count.longValue());
    }

    /**
     * 返回集合中一个或多个随机数
     *
     * @param key   key
     * @param count 数量
     */
    public List<Object> sRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().randomMembers(key, count);
    }

    /**
     * 返回集合中一个或多个随机数
     *
     * @param key   key
     * @param count 数量
     */
    public List<Object> sRandomMembers(String key, Integer count) {
        return redisTemplate.opsForSet().randomMembers(key, count.longValue());
    }

    /**
     * 返回集合中一个或多个随机数
     *
     * @param key   key
     * @param count 数量
     */
    public List<Object> sRandomMembers(String key, Double count) {
        return redisTemplate.opsForSet().randomMembers(key, count.longValue());
    }

    /**
     * 迭代集合中的元素
     *
     * @param key                key
     * @param count              count
     * @param pattern            pattern
     * @param scriptObjectMirror 回调函数
     */
    public void sScan(String key, long count, String pattern, ScriptObjectMirror scriptObjectMirror) {
        ScriptObjectMirror callback = InternalUtils.getCallback(scriptObjectMirror);
        ScanOptions scanOptions = ScanOptions.scanOptions().count(count).match(pattern).build();
        Cursor<Object> cursor = redisTemplate.opsForSet().scan(key, scanOptions);
        while (cursor.hasNext()) {
            Object value = cursor.next();
            Object res = callback.call(value, value);
            if (res instanceof Boolean && (Boolean) res) {
                break;
            }
        }
    }

    /**
     * 迭代集合中的元素
     *
     * @param key                key
     * @param count              count
     * @param pattern            pattern
     * @param scriptObjectMirror 回调函数
     */
    public void sScan(String key, Integer count, String pattern, ScriptObjectMirror scriptObjectMirror) {
        sScan(key, count.longValue(), pattern, scriptObjectMirror);
    }

    /**
     * 迭代集合中的元素
     *
     * @param key                key
     * @param count              count
     * @param pattern            pattern
     * @param scriptObjectMirror 回调函数
     */
    public void sScan(String key, Double count, String pattern, ScriptObjectMirror scriptObjectMirror) {
        sScan(key, count.longValue(), pattern, scriptObjectMirror);
    }

    // --------------------------------------------------------------------------------------------
    // Sorted Set 操作
    // --------------------------------------------------------------------------------------------


    public void tt(String key, Object value) {
        redisTemplate.boundValueOps(key).set(value);
//        redisTemplate.opsForZSet()
//        redisTemplate.opsForHyperLogLog()
//        redisTemplate.opsForGeo()
//        redisTemplate.opsForCluster()
//        redisTemplate.execute()
//        redisTemplate.executePipelined()
    }
}
