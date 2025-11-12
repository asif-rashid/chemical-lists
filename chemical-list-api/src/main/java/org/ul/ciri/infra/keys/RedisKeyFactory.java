package org.ul.ciri.infra.keys;

import org.springframework.stereotype.Component;

@Component
public class RedisKeyFactory {

    public String listKey(String suffix) {
        return "list:" + suffix;
    }

    public String membershipKey(String listId) {
        return "membership:" + listId;
    }

    public String idempotencyKey(String key) {
        return "idempotency:" + key;
    }
}
