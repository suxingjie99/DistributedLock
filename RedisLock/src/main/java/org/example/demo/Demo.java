package org.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Demo {

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 版本1:
     * public void test(){
     *      String lock = getLock("lock");
     *      if(lock==null){
     *          set("lock","1")
     *          //执行业务
     *          del("lock")
     *      }else{
     *          while(true){
     *              test()
     *          }
     *      }
     * }
     * 缺点:加锁不是原子性
     *
     * 版本2:
     * public void test(){
     *      Integer lock = setnx("lock","1");
     *      if(lock!=0){
     *          //执行业务
     *          del("lock")
     *      }else{
     *          while(true){
     *              test()
     *          }
     *      }
     * }
     * 缺点:由于各种问题锁没有释放,则其他线程永远无法获取到锁
     *
     * 版本3:
     * public void test(){
     *      Integer lock = setnx("lock","1");
     *      if(lock!=0){
     *          expire("locks",10s)
     *          //执行业务
     *          del("lock")
     *      }else{
     *          while(true){
     *              test()
     *          }
     *      }
     * }
     * 缺点:加锁和设置过期时间不是原子性
     *
     * 版本4:
     * public void test(){
     *      Integer lock = setexnx("lock","1",10s);
     *      if(lock!=0){
     *          //执行业务
     *          del("lock")
     *      }else{
     *          while(true){
     *              test()
     *          }
     *      }
     * }
     * 缺点:如果线程1获取锁,执行业务花了12s,锁过期删除,另一个线程2获取到锁,线程1执行del("lock")将线程2的锁删除
     *
     * 版本5:
     * public void test(){
     *      String token = UUID.randomUUID().toString();
     *      Integer lock = setexnx("lock",token,10s);
     *      if(lock!=0){
     *          //执行业务
     *          if(get("lock")==token){
     *              del("lock")
     *          }
     *      }else{
     *          while(true){
     *              test()
     *          }
     *      }
     * }
     * 缺点:我们获取锁的时候,锁过期,我们删除的就是别人加的锁.
     *
     * 版本6:
     * public void test(){
     *      String token = UUID.randomUUID().toString();
     *      Integer lock = setexnx("lock",token,10s);
     *      if(lock!=0){
     *          //执行业务
     *          //脚本删除锁
     *      }else{
     *          while(true){
     *              test()
     *          }
     *      }
     * }
     *
     *
     *
     */


    public void test01(){
        String token = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS);
        if (lock){





        String script ="if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end" ;
            DefaultRedisScript<Boolean> script1 = new DefaultRedisScript<>(script);
            redisTemplate.execute(script1, Arrays.asList("lock"),token);
        }else {
            while (true){
                test01();
            }
        }

    }
}
