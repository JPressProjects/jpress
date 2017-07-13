package io.jpress.searcher;

import java.util.*;

import redis.clients.jedis.*;

/**
 * Created by francis on 2017/7/13.
 */
public class RedisHelper {
    public static final String SUCCESS_OK = "OK";
    public static RedisHelper INSTANCE = null;

    static {
        String ip = Redisearcher.ip;
        int port = Redisearcher.port;
        String password = Redisearcher.password;
        int database = 0;
        INSTANCE = new RedisHelper(ip, port, password, database);
    }

    // Redis服务器IP
    private String ip;
    // Redis的端口号
    private int port;
    // 访问密码
    private String auth;
    // 可用连接实例的最大数目，默认值为8；
    // 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private int MAX_ACTIVE = 1024;
    // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private int MAX_IDLE = 200;
    // 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private int MAX_WAIT = 10000;
    private int TIMEOUT = 10000;
    // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private boolean TEST_ON_BORROW = true;
    private JedisPool jedisPool = null;

    /**
     * 初始化Redis连接池
     */
    public RedisHelper(String ip, int port, String password, int database) {
        this.ip = ip;
        this.port = port;
        auth = password;
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(MAX_ACTIVE);
        config.setMaxIdle(MAX_IDLE);
        config.setMaxWaitMillis(MAX_WAIT);
        config.setTestOnBorrow(TEST_ON_BORROW);
        try {
            if (database <= 0) {
                if (password == null || password.length() <= 0) {
                    jedisPool = new JedisPool(config, this.ip, this.port, TIMEOUT);
                } else {
                    jedisPool = new JedisPool(config, this.ip, this.port, TIMEOUT, auth);
                }
            } else {
                jedisPool = new JedisPool(config, this.ip, this.port, TIMEOUT, auth, database);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Jedis实例
     *
     * @return
     */
    public Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                System.out.println("getJedis()-error-1");
                return null;
            }
        } catch (Exception e) {
            System.out.println("getJedis()-error-2");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 释放jedis资源
     *
     * @param jedis
     */
    public void returnResource(final Jedis jedis) {
        if (jedis != null) {
            jedisPool.close();
        }
    }

    public Jedis getResource() {
        Jedis client = getJedis();
        return client;
    }

    public int set(String key, String value) {
        Jedis client = null;
        try {
            client = getJedis();
            client.set(key, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }

        return 1;
    }

    public int set(String key, String value, int expireSeconds) {
        Jedis client = null;
        try {
            client = getJedis();
            client.set(key, value);
            client.expire(key, expireSeconds);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
        return 1;
    }

    public long setnx(String key, String value) {
        Jedis client = null;
        long res = -1;
        try {
            client = getJedis();
            res = client.setnx(key, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
        return res;
    }

    public int expire(String key, int expireSeconds) {
        Jedis client = null;
        try {
            client = getJedis();
            client.expire(key, expireSeconds);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
        return 1;
    }

    public boolean exists(String key) {
        Jedis client = null;
        boolean result = true;
        try {
            client = getJedis();
            result = client.exists(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
        return result;
    }

    public void delete(String key) {
        Jedis client = null;
        try {
            client = getJedis();
            client.del(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
    }

    public String get(String key) {
        Jedis client = null;
        String value = "";
        try {
            client = getJedis();
            value = client.get(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
        return value;
    }

    public int getMAX_ACTIVE() {
        return MAX_ACTIVE;
    }

    public void setMAX_ACTIVE(int MAX_ACTIVE) {
        this.MAX_ACTIVE = MAX_ACTIVE;
    }

    public int getMAX_IDLE() {
        return MAX_IDLE;
    }

    public void setMAX_IDLE(int MAX_IDLE) {
        this.MAX_IDLE = MAX_IDLE;
    }

    public int getMAX_WAIT() {
        return MAX_WAIT;
    }

    public void setMAX_WAIT(int MAX_WAIT) {
        this.MAX_WAIT = MAX_WAIT;
    }

    public int getTIMEOUT() {
        return TIMEOUT;
    }

    public void setTIMEOUT(int TIMEOUT) {
        this.TIMEOUT = TIMEOUT;
    }

    public boolean isTEST_ON_BORROW() {
        return TEST_ON_BORROW;
    }

    public void setTEST_ON_BORROW(boolean TEST_ON_BORROW) {
        this.TEST_ON_BORROW = TEST_ON_BORROW;
    }

    /****************************
     * redis 列表List start
     ***************************/

    /**
     * 将一个值插入到列表头部，value可以重复，返回列表的长度
     *
     * @param key
     * @param value String
     * @return 返回List的长度
     */
    public Long lpush(String key, String value) {
        Jedis jedis = null;
        long length = 0l;
        try {
            jedis = jedisPool.getResource();
            length = jedis.lpush(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
        return length;
    }

    /**
     * 将多个值插入到列表头部，value可以重复
     *
     * @param key
     * @param values String[]
     * @return 返回List的数量size
     */
    public Long lpush(String key, String[] values) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long size = jedis.lpush(key, values);
            return size;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
        return 0L;
    }

    /**
     * 获取List列表
     *
     * @param key
     * @param start long，开始索引
     * @param end   long， 结束索引
     * @return List
     */
    public List lrange(String key, long start, long end) {
        Jedis jedis = null;
        List list = null;
        try {
            jedis = jedisPool.getResource();
            list = jedis.lrange(key, start, end);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
        return list;
    }

    public boolean isMemberinList(String key, String value) {
        Jedis jedis = null;
        boolean ifExist;
        try {
            jedis = jedisPool.getResource();
            ifExist = jedis.sismember(key, value);
        } finally {
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }

        return ifExist;
    }

    /**
     * 通过索引获取列表中的元素
     *
     * @param key
     * @param index，索引，0表示最新的一个元素
     * @return String
     */
    public String lindex(String key, long index) {
        Jedis jedis = null;
        String str = "";
        try {
            jedis = jedisPool.getResource();
            str = jedis.lindex(key, index);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
        return str;
    }

    /**
     * 获取列表长度，key为空时返回0
     *
     * @param key
     * @return Long
     */
    public Long llen(String key) {
        Jedis jedis = null;
        Long length = 0L;
        try {
            jedis = jedisPool.getResource();
            length = jedis.llen(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
        return length;
    }

    /**
     * 在列表的元素前或者后插入元素，返回List的长度
     *
     * @param key
     * @param where LIST_POSITION
     * @param pivot 以该元素作为参照物，是在它之前，还是之后（pivot：枢轴;中心点，中枢;[物]支点，支枢;[体]回转运动。）
     * @param value
     * @return Long
     */
    public Long linsert(String key, BinaryClient.LIST_POSITION where, String pivot, String value) {
        Jedis jedis = null;
        Long length = 0L;
        try {
            jedis = jedisPool.getResource();
            length = jedis.linsert(key, where, pivot, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
        return length;
    }

    /**
     * 将一个或多个值插入到已存在的列表头部，当成功时，返回List的长度；当不成功（即key不存在时，返回0）
     *
     * @param key
     * @param value String
     * @return Long
     */
    public Long lpushx(String key, String value) {
        Jedis jedis = null;
        Long length = 0L;
        try {
            jedis = jedisPool.getResource();
            length = jedis.lpushx(key, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }

        return length;
    }

    /**
     * 将一个或多个值插入到已存在的列表头部，当成功时，返回List的长度；当不成功（即key不存在时，返回0）
     *
     * @param key
     * @param values String[]
     * @return Long
     */
    public Long lpushx(String key, String[] values) {
        Jedis jedis = null;
        Long length = 0L;
        try {
            jedis = jedisPool.getResource();
            length = jedis.lpushx(key, values);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return length;
    }

    /**
     * 移除列表元素，返回移除的元素数量
     *
     * @param key
     * @param count，标识，表示动作或者查找方向当count=0时，移除所有匹配的元素； 当count为负数时，移除方向是从尾到头；
     *                                                当count为正数时，移除方向是从头到尾；
     * @param value                                   匹配的元素
     * @return Long
     */
    public Long lrem(String key, long count, String value) {
        Jedis jedis = null;
        Long length = 0L;
        try {
            jedis = jedisPool.getResource();
            length = jedis.lrem(key, count, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return length;
    }

    /**
     * 通过索引设置列表元素的值，当超出索引时会抛错。成功设置返回true
     *
     * @param key
     * @param index 索引
     * @param value
     * @return boolean
     */

    public boolean lset(String key, long index, String value) {
        Jedis jedis = null;
        String statusCode = "";
        try {
            jedis = jedisPool.getResource();
            statusCode = jedis.lset(key, index, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (SUCCESS_OK.equalsIgnoreCase(statusCode)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
     *
     * @param key
     * @param start 可以为负数（-1是列表的最后一个元素，-2是列表倒数第二的元素。） 如果start大于end，则返回一个空的列表，即列表被清空
     * @param end   可以为负数（-1是列表的最后一个元素，-2是列表倒数第二的元素。）   可以超出索引，不影响结果
     * @return boolean
     */
    public boolean ltrim(String key, long start, long end) {
        Jedis jedis = null;
        String statusCode = "";
        try {
            jedis = jedisPool.getResource();
            statusCode = jedis.ltrim(key, start, end);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (SUCCESS_OK.equalsIgnoreCase(statusCode)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 移出并获取列表的第一个元素，当列表不存在或者为空时，返回Null
     *
     * @param key
     * @return String
     */
    public String lpop(String key) {
        Jedis jedis = null;
        String value = "";
        try {
            jedis = jedisPool.getResource();
            value = jedis.lpop(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return value;
    }

    /**
     * 移除并获取列表最后一个元素，当列表不存在或者为空时，返回Null
     *
     * @param key
     * @return String
     */
    public String rpop(String key) {
        Jedis jedis = null;
        String value = "";
        try {
            jedis = jedisPool.getResource();
            value = jedis.rpop(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return value;
    }

    /**
     * 在列表中的尾部添加一个个值，返回列表的长度
     *
     * @param key
     * @param value
     * @return Long
     */
    public Long rpush(String key, String value) {
        Jedis jedis = null;
        Long length = 0L;
        try {
            jedis = jedisPool.getResource();
            length = jedis.rpush(key, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return length;
    }

    /**
     * 在列表中的尾部添加多个值，返回列表的长度
     *
     * @param key
     * @param values
     * @return Long
     */
    public Long rpush(String key, String[] values) {
        Jedis jedis = null;
        Long length = 0l;
        try {
            jedis = jedisPool.getResource();
            Pipeline p = jedis.pipelined();
            jedis.del(key);
            length = jedis.rpush(key, values);
            p.sync();
            p.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return length;
    }

    /**
     * 仅当列表存在时，才会向列表中的尾部添加一个值，返回列表的长度
     *
     * @param key
     * @param value
     * @return Long
     */
    public Long rpushx(String key, String value) {
        Jedis jedis = null;
        Long length = 0L;
        try {
            jedis = jedisPool.getResource();
            length = jedis.rpushx(key, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return length;
    }

    /**
     * 移除列表的最后一个元素，并将该元素添加到另一个列表并返回
     *
     * @param sourceKey 源列表的key，当源key不存在时，结果返回Null
     * @param targetKey 目标列表的key，当目标key不存在时，会自动创建新的
     * @return String
     */
    public String rpopLpush(String sourceKey, String targetKey) {
        Jedis jedis = null;
        String value = "";
        try {
            jedis = jedisPool.getResource();
            value = jedis.rpoplpush(sourceKey, targetKey);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return value;
    }

    /**
     * 移出并获取列表的【第一个元素】， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     *
     * @param timeout 单位为秒
     * @param keys    当有多个key时，只要某个key值的列表有内容，即马上返回，不再阻塞。 当所有key都没有内容或不存在时，则会阻塞，直到有值返回或者超时。
     *                当超期时间到达时，keys列表仍然没有内容，则返回Null
     * @return List
     */
    public List blpop(int timeout, String... keys) {
        Jedis jedis = null;
        List values = new ArrayList<>();
        try {
            jedis = jedisPool.getResource();
            values = jedis.blpop(timeout, keys);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return values;
    }

    /**
     * 移出并获取列表的【最后一个元素】， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     *
     * @param timeout 单位为秒
     * @param keys    当有多个key时，只要某个key值的列表有内容，即马上返回，不再阻塞。 当所有key都没有内容或不存在时，则会阻塞，直到有值返回或者超时。
     *                当超期时间到达时，keys列表仍然没有内容，则返回Null
     * @return List
     */
    public List brpop(int timeout, String... keys) {
        Jedis jedis = null;
        List values = new ArrayList<>();
        try {
            jedis = jedisPool.getResource();
            values = jedis.brpop(timeout, keys);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return values;
    }

    /**
     * 从列表中弹出列表最后一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     *
     * @param sourceKey 源列表的key，当源key不存在时，则会进行阻塞
     * @param targetKey 目标列表的key，当目标key不存在时，会自动创建新的
     * @param timeout   单位为秒
     * @return String
     */
    public String brpopLpush(String sourceKey, String targetKey, int timeout) {
        Jedis jedis = null;
        String value = "";
        try {
            jedis = jedisPool.getResource();
            value = jedis.brpoplpush(sourceKey, targetKey, timeout);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return value;
    }

    public boolean getBit(String key, long offset) {
        Jedis jedis = null;
        boolean result = false;
        try {
            jedis = jedisPool.getResource();
            result = jedis.getbit(key, offset);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        return result;
    }

    public boolean setBit(String key, long offset, boolean value) {
        Jedis jedis = null;
        boolean result = false;
        try {
            jedis = jedisPool.getResource();
            result = jedis.setbit(key, offset, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }

    public long bitcount(String key) {
        Jedis jedis = null;
        long result = 0;
        try {
            jedis = jedisPool.getResource();
            result = jedis.bitcount(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    /**************************** redis 列表List end ***************************/

    /****************************
     * redis hash table start
     ***************************/
    /**
     * 将map中的值添加到key对应的hash table中
     *
     * @param key
     * @param map
     * @throws Exception
     */
    public void hmset(String key, Map<String, String> map) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hmset(key, map);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 计算key对应的hashtable 长度
     *
     * @param key
     * @return
     * @throws Exception
     */
    public Long hlen(String key) throws Exception {
        Jedis jedis = null;
        Long length = 0L;
        try {
            jedis = jedisPool.getResource();
            length = jedis.hlen(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return length;
    }

    /**
     * 返回哈希表 key 中的所有域。
     *
     * @param key
     * @return
     * @throws Exception
     */
    public Set hkeys(String key) throws Exception {
        Jedis jedis = null;
        Set tmpResult = new HashSet<>();
        try {
            jedis = jedisPool.getResource();
            tmpResult = jedis.hkeys(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return tmpResult;
    }

    /**
     * 返回哈希表 key 中所有域的值。
     *
     * @param key
     * @return
     * @throws Exception
     */
    public List hvals(String key) throws Exception {
        Jedis jedis = null;
        List tmpResult = new ArrayList<>();
        try {
            jedis = jedisPool.getResource();
            tmpResult = jedis.hvals(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return tmpResult;
    }

    /**
     * 返回哈希表 key 中，一个或多个给定域的值。
     * 如果给定的域不存在于哈希表，那么返回一个 nil 值。
     * 因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
     *
     * @param key
     * @param s1
     * @return
     * @throws Exception
     */
    public List hmget(String key, String... s1) {
        Jedis jedis = null;
        List tmpResult = new ArrayList<>();
        try {
            jedis = jedisPool.getResource();
            tmpResult = jedis.hmget(key, s1);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return tmpResult;
    }

    /**
     * 返回哈希表 key 中，一个给定域的值。
     * 如果给定的域不存在于哈希表，那么返回一个 nil 值。
     * 因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
     *
     * @param key
     * @param s1
     * @return
     * @throws Exception
     */
    public String hget(String key, String s1) {
        Jedis jedis = null;
        String strResult = "";
        try {
            jedis = jedisPool.getResource();
            strResult = jedis.hget(key, s1);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return strResult;
    }

    /**
     * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
     *
     * @param key
     * @param s
     * @throws Exception
     */
    public void hdel(String key, String s) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hdel(key, s);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public Map<String, String> hgetall(String key) {
        Jedis jedis = null;
        Map<String, String> tmpResult = new HashMap<>();
        try {
            jedis = jedisPool.getResource();
            tmpResult = jedis.hgetAll(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return tmpResult;
    }

    public void hset(String key, String field, String val) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hset(key, field, val);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 为哈希表 key 中的域 field 的值加上增量 increment
     * 增量也可以为负数，相当于对给定域进行减法操作。
     * 如果 key 不存在，一个新的哈希表被创建并执行 HINCRBY 命令。
     * 如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。
     * 对一个储存字符串值的域 field 执行 HINCRBY 命令将造成一个错误。
     * 本操作的值被限制在 64 位(bit)有符号数字表示之内。
     *
     * @param key
     * @param field
     * @param changedValue
     */
    public void hincrBy(String key, String field, long changedValue) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hincrBy(key, field, changedValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    /****************************
     * redis hash table end
     ***************************/

    /****************************
     * redis sorted set start
     ***************************/

    /**
     * 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
     *
     * @param key
     * @param score 权重
     * @param value 值
     * @return 返回新成员的总数
     */
    public long zadd(String key, double score, String value) {
        Jedis jedis = null;
        long addedCount = 0;
        try {
            jedis = jedisPool.getResource();
            addedCount = jedis.zadd(key, score, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return addedCount;
    }

    /**
     * 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
     *
     * @param key
     * @param memberScores key=值 value=权重
     * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
     */
    public long zmadd(String key, Map<String, Double> memberScores) {
        Jedis jedis = null;
        long n = 0;
        try {
            jedis = jedisPool.getResource();
            n = jedis.zadd(key, memberScores);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return n;
    }

    /**
     * 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
     *
     * @param key
     * @param member 成员信息
     * @param score  权重值
     * @return 返回新成员的总数
     */
    public long zadd(String key, String member, double score) {
        Jedis jedis = null;
        long n = 0;
        try {
            jedis = jedisPool.getResource();
            n = jedis.zadd(key, score, member);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return n;
    }

    /**
     * 返回有序集 key 的基数。
     *
     * @param key
     * @return
     */
    public long zcard(String key) {
        Jedis jedis = null;
        long n = 0;
        try {
            jedis = jedisPool.getResource();
            n = jedis.zcard(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return n;
    }

    /**
     * 返回有序集 key 中，指定区间内的成员。
     * 其中成员的位置按 score 值递增(从小到大)来排序。
     * 如果你需要成员按 score 值递减(从大到小)来排列，请使用 ZREVRANGE 命令。
     *
     * @param key
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @return
     */
    public Set zrange(String key, long startIndex, long endIndex) {
        Jedis jedis = null;
        Set tmpResult = new HashSet<>();
        try {
            jedis = jedisPool.getResource();
            tmpResult = jedis.zrange(key, startIndex, endIndex);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return tmpResult;
    }

    /**
     * 返回有序集 key 中，指定区间内的成员。
     * 其中成员的位置按 score 值递增(从大到小)来排序。
     * 如果你需要成员按 score 值递减(从小到大)来排列，请使用 zrange 命令。
     *
     * @param key
     * @param startIndex
     * @param endIndex
     * @return
     */
    public Set zrevrange(String key, long startIndex, long endIndex) {
        Jedis jedis = null;
        Set tmpResult = new HashSet<>();
        try {
            jedis = jedisPool.getResource();
            tmpResult = jedis.zrevrange(key, startIndex, endIndex);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return tmpResult;
    }

    /**
     * 移除有序集 key 中，指定排名(rank)区间内的所有成员。
     * 区间分别以下标参数 startIndex 和 endIndex 指出，包含 startIndex 和 endIndex 在内。
     *
     * @param key
     * @param startIndex
     * @param endIndex
     * @return 被移除成员的数量
     */
    public long zremrangeByRank(String key, long startIndex, long endIndex) {
        Jedis jedis = null;
        long removedCount = 0;
        try {
            jedis = jedisPool.getResource();
            removedCount = jedis.zremrangeByRank(key, startIndex, endIndex);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return removedCount;
    }

    /**
     * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略。
     *
     * @param key    key
     * @param member 成员
     * @return 被成功移除的成员的数量，不包括被忽略的成员
     */
    public long zrem(String key, String member) {
        Jedis jedis = null;
        long removedCount = 0;
        try {
            jedis = jedisPool.getResource();
            removedCount = jedis.zrem(key, member);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return removedCount;
    }

    /**
     * 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
     *
     * @param key
     * @param min 最低权值
     * @param max 最高权值
     * @return 被移除成员的数量
     */
    public long zremrangeByScore(String key, double min, double max) {
        Jedis jedis = null;
        long leftTotalCount = 0;
        try {
            jedis = jedisPool.getResource();
            leftTotalCount = jedis.zremrangeByScore(key, min, max);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return leftTotalCount;
    }

    /**
     * 执行脚本
     *
     * @param scripts 脚本内容
     * @param params  参数， 使用逗号分割开
     * @return
     */
    public Object eval(String scripts, String... params) {
        Jedis jedis = null;
        Object tmpResult = null;
        try {
            jedis = jedisPool.getResource();
            tmpResult = jedis.eval(scripts, params != null ? params.length : 0, params);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return tmpResult;
    }

    /**
     * 加载脚本信息
     *
     * @param scripts 脚本
     * @return 返回保存脚本的SHA
     */
    public String loadScript(String scripts) {
        Jedis jedis = null;
        String tmpSHA = null;
        try {
            jedis = jedisPool.getResource();
            tmpSHA = jedis.scriptLoad(scripts);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return tmpSHA;
    }

    /**
     * 使用脚本的SHA值 执行脚本调用
     *
     * @param scriptsSHA 脚本的SHA值， 调用loadScript会返回
     * @param params     参数
     * @return
     */
    public Object evalSHA(String scriptsSHA, String... params) {
        Jedis jedis = null;
        Object tmpResult = null;
        try {
            jedis = jedisPool.getResource();
            tmpResult = jedis.evalsha(scriptsSHA, params != null ? params.length : 0, params);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (jedis != null) {
                    jedis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return tmpResult;
    }
    /****************************
     * redis sorted set end
     ***************************/

    /**
     * 在多少秒内是否昨个相同动作
     *
     * @param actionName  动作名字（不同的动作名字必须不同， 不区分大小写）
     * @param withSeconds 在多少秒内操作太多次
     * @return
     */
    public boolean areYouDointActionTooFast(String actionName, int withSeconds) {
        String strLockKey = String.format("%s_Lock", actionName);
        Jedis jedis = null;
        try {
            jedis = getResource();
            long count = jedis.incr(strLockKey);
            if (count == 1) {
                jedis.expire(strLockKey, withSeconds);
            }
            if (count > 1) {
                return true;
            }
        } catch (Exception ex) {
            if (jedis != null) {
                try {
                    jedis.del(strLockKey);
                } finally {

                }
            }
        } finally {
            if (jedis != null) {
                try {
                    jedis.close();
                } finally {

                }
            }
        }
        return false;
    }
}
