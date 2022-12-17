package org.kin.reactor.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author huangjianqin
 * @date 2022/12/17
 */
public class Payload {
    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    private Map<String, Object> data;
    private int clientId = COUNTER.getAndIncrement();

    public Payload() {
        this.data = new HashMap<String, Object>() {
            {
                put("c1", ThreadLocalRandom.current().nextInt(100));
                put("c2", new HashMap<String, Object>() {
                    {
                        put("d1", 1000 + ThreadLocalRandom.current().nextInt(1000));
                    }
                });
            }
        };
    }

    //setter && getter
    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "Payload{" +
                "data=" + data +
                ", clientId=" + clientId +
                '}';
    }
}
