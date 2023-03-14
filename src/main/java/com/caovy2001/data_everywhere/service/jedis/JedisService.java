package com.caovy2001.data_everywhere.service.jedis;

import com.caovy2001.data_everywhere.service.BaseService;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
public class JedisService extends BaseService implements IJedisService {
    private static final Jedis jedis = new Jedis("redis://default:DP52KjeuwAhZyjKrpvP3kBlOurft4mHi@redis-10810.c263.us-east-1-2.ec2.cloud.redislabs.com:10810");
    @Override
    public String get(String key) {
        return jedis.get(key);
    }

    @Override
    public void setWithExpired(String key, String value, long seconds) {
        jedis.setex(key, seconds, value);
    }
}
