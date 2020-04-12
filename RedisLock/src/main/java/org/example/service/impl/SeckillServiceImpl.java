package org.example.service.impl;

import org.example.lock.RedisLock;
import org.example.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
/**
 * @author su
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisLock redisLock;



    @Override
    public void seckill() {
        redisLock.lock();
        Integer stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
        try {
            if (stock>0){
                System.out.println("抢购成功");
                stringRedisTemplate.opsForValue().decrement("stock",1);
                System.out.println("当前剩余:"+(stock-1));
            }else {
                System.out.println("抢购失败");
            }
        }finally {
            redisLock.unlock();
        }


    }
}
