package com.example.demoiocaop.IOC;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.simpleframework.xml.core.Commit;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class RedisLock {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * @param key
     * @param value 当前时间+超时时间
     * @return
     */

    public boolean lock(String key,String value){
        if(redisTemplate.opsForValue().setIfAbsent(key,value)){
            return true;
        }
        //抛了异常时，无法解锁
        String currentValue = redisTemplate.opsForValue().get(key);
        if(!StringUtils.isEmpty(currentValue)
                && Long.parseLong(currentValue)<System.currentTimeMillis()){
            String oldValue = redisTemplate.opsForValue().getAndSet(key,value);
            if(!StringUtils.isEmpty(oldValue)
                    && oldValue.equals(currentValue))
                return true;
        }
        return false;
    }


    public void unlock(String key,String value){
        try{
            String currentValue = redisTemplate.opsForValue().get(key);
            if(!StringUtils.isEmpty(currentValue) && currentValue.equals(value)){
                redisTemplate.opsForValue().getOperations().delete(key);
            }
        }catch (Exception e){
            log.error("解锁异常",e);
        }

    }


    //加锁

    //解锁
}
