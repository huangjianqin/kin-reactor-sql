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
        ReactorSql.create("select c2 from test")
                .prepare()
                .apply(Flux.fromIterable(Arrays.asList(
                        new HashMap<String, Object>(){
                            {
                                put("c1", 1);
                            }
                        },
                        new HashMap<String, Object>(){
                            {
                                put("c1", 2);
                            }
                        },
                        new HashMap<String, Object>(){
                            {
                                put("c1", 3);
                            }
                        },
                        new HashMap<String, Object>(){
                            {
                                put("c1", 4);
                            }
                        },
                        new HashMap<String, Object>(){
                            {
                                put("c1", 5);
                            }
                        }
                )))
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void test2() {
        int num = 10;
        List<Column> columns = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            columns.add(new Column());
        }

        ReactorSql.create("select * from test")
                .prepare()
                .apply(Flux.fromIterable(columns))
                .doOnNext(System.out::println)
                .as(StepVerifier::create)
                .expectNextCount(num)
                .verifyComplete();
    }
}
