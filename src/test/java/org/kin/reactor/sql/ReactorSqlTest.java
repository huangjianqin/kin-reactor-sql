package org.kin.reactor.sql;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author huangjianqin
 * @date 2022/12/15
 */
public class ReactorSqlTest {
    @Test
    void test0() {
        ReactorSql.create("select * from test")
                .prepare()
                .apply(Flux.range(0, 20))
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextCount(20)
                .verifyComplete();
    }

    @Test
    void test1() {
        ReactorSql.create("select this.c2.d1 from test where this.c2.d1 > 25")
                .prepare()
                .apply(Flux.fromIterable(Arrays.asList(
                        new HashMap<String, Object>() {
                            {
                                put("c1", 1);
                                put("c2", new HashMap<String, Object>() {
                                    {
                                        put("d1", 10);
                                    }
                                });
                            }
                        },
                        new HashMap<String, Object>() {
                            {
                                put("c1", 2);
                                put("c2", new HashMap<String, Object>() {
                                    {
                                        put("d1", 20);
                                    }
                                });
                            }
                        },
                        new HashMap<String, Object>() {
                            {
                                put("c1", 3);
                                put("c2", new HashMap<String, Object>() {
                                    {
                                        put("d1", 30);
                                    }
                                });
                            }
                        },
                        new HashMap<String, Object>() {
                            {
                                put("c1", 4);
                                put("c2", new HashMap<String, Object>() {
                                    {
                                        put("d1", 40);
                                    }
                                });
                            }
                        },
                        new HashMap<String, Object>() {
                            {
                                put("c1", 5);
                                put("c2", new HashMap<String, Object>() {
                                    {
                                        put("d1", 50);
                                    }
                                });
                            }
                        }
                )))
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void test2() {
        int num = 10;
        List<Column> columns = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            columns.add(new Column());
        }

        ReactorSql.create("select a+10 as a1, a, now() from test")
                .prepare()
                .apply(Flux.fromIterable(columns))
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextCount(num)
                .verifyComplete();
    }
}
