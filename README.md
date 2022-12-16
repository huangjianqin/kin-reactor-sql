# reactor-sql
基于sql处理数据流

[Reactor3](https://github.com/reactor) + [JSqlParser](https://github.com/JSQLParser/JSqlParser) = reactor-sql

## 特性
1. 支持字段映射
2. 支持多种数学函数, 时间函数等等, 详情请看`org.kin.reactor.sql.GlobalFeatures`
3. 支持字段运算(`v+1`)及条件判断(`case when`)

用例:
```java
public class Demo{
    public static void main(String[]args){
        int num=10;
        List<Column> columns=new ArrayList<>();
        for(int i=0;i<num; i++){
            columns.add(new Column());
        }

        ReactorSql.create("select * from demo")
                .prepare()
                .apply(Flux.fromIterable(columns))
                .doOnNext(System.out::println)
                .subscribe();
    }
}
```

## Reference
* [reactor-ql](https://github.com/jetlinks/reactor-ql), Reactor3 + JSqlParser实现基于sql处理数据流, 支持特性丰富, 包括join, group by和聚合等等