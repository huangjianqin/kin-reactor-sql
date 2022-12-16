package org.kin.reactor.sql;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author huangjianqin
 * @date 2022/12/16
 */
public class Column {
    private static final char[] CHARS = new char[]{'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', '[', ']', '{', '}', '\\', '|',
            'a', 'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ';', ':', '\'', '\"',
            'z', 'x', 'c', 'v', 'b', 'n', 'm', ',', '<', '.', '>', '/', '?', ' '};

    private int a;
    private long b;
    private double c;
    private String d = "";
    private Date date = new Date();
    private List<Integer> list = new ArrayList<>();
    private Map<String, Object> map = new HashMap<>();

    public Column() {
        this.a = ThreadLocalRandom.current().nextInt(100);
        this.b = 1000 + ThreadLocalRandom.current().nextLong(10000);
        this.c = 100000 + ThreadLocalRandom.current().nextDouble(10000);

        for (int i = 0; i < 5 + ThreadLocalRandom.current().nextInt(20); i++) {
            d += CHARS[ThreadLocalRandom.current().nextInt(CHARS.length)];
            map.put(i + "", d);
        }

        for (int i = 0; i < 5 + ThreadLocalRandom.current().nextInt(20); i++) {
            list.add(ThreadLocalRandom.current().nextInt(100000));
            map.put(ThreadLocalRandom.current().nextInt(100000) + "", ThreadLocalRandom.current().nextInt(100000));
        }
    }

    //setter && getter
    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public long getB() {
        return b;
    }

    public void setB(long b) {
        this.b = b;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "Column{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                ", d='" + d + '\'' +
                ", date=" + date +
                ", list=" + list +
                ", map=" + map +
                '}';
    }
}
