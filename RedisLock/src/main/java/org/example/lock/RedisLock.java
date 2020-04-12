package org.example.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author su
 */
@Component
public class RedisLock implements Lock {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    ThreadLocal<String> threadLocal=  new ThreadLocal<>();

    @Override
    public void lock() {
        String token = UUID.randomUUID().toString();
        threadLocal.set(token);
        while (true){
            Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", token, 10, TimeUnit.SECONDS);
            if (lock){
                return;
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        String token = threadLocal.get();
        String script ="if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end" ;
        DefaultRedisScript<Boolean> script1 = new DefaultRedisScript<>(script,Boolean.class);
        stringRedisTemplate.execute(script1, Arrays.asList("lock"),token);
        threadLocal.remove();
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
