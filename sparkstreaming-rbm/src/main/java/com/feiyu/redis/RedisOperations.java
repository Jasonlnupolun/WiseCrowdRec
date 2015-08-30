package com.feiyu.redis;
/**
 * @author Fei Yu (@faustineinsun)
 */

public class RedisOperations {
  /*
  public void cacheAKeyInCertainTime(String key, String value) {
    int expireTime = 300; // 5 mins 60*5
    GlobalVariables.JEDIS_API.set(key, value);
    GlobalVariables.JEDIS_API.expire(key, expireTime);
  }

  // TTL: time to live, the remaining time in seconds
  public Long getTTLOfKey(String key) {
    return GlobalVariables.JEDIS_API.ttl(key);
  }

  public void cacheLatestActorMoviesGenreGraphJson(String json) {
    GlobalVariables.JEDIS_API.set("latest:AMG:Graph:Json:key", json);
  }

  public String getLatestActorMoviesGenreGraphJson() {
    return GlobalVariables.JEDIS_API.get("latest:AMG:Graph:Json:key");
  }

  public void countAKey() {}

  // Later cache Lists, Hashs, set and others
   * 
   */
}
