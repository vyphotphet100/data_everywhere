package com.caovy2001.data_everywhere.service.jedis;

import com.caovy2001.data_everywhere.service.BaseService;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
public class JedisService extends BaseService implements IJedisService {
    private static final String jedisConnectionStr = "redis://default:DP52KjeuwAhZyjKrpvP3kBlOurft4mHi@redis-10810.c263.us-east-1-2.ec2.cloud.redislabs.com:10810";
//    private static final Jedis jedis = new Jedis("redis://default:DP52KjeuwAhZyjKrpvP3kBlOurft4mHi@redis-10810.c263.us-east-1-2.ec2.cloud.redislabs.com:10810");
    @Override
    public String get(String key) {
        Jedis jedis = new Jedis(jedisConnectionStr);
        String result = jedis.get(key);
        jedis.close();
        return result;
    }

    @Override
    public void setWithExpired(String key, String value, long seconds) {
        Jedis jedis = new Jedis(jedisConnectionStr);
        jedis.setex(key, seconds, value);
        jedis.close();
    }
}
