package com.caovy2001.data_everywhere.service.jedis;

import com.caovy2001.data_everywhere.service.IBaseService;

public interface IJedisService extends IBaseService {
    String get(String key);
    void setWithExpired(String key, String value, long seconds);
}
