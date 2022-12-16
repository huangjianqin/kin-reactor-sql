package org.kin.reactor.sql;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author huangjianqin
 * @date 2022/12/16
 */
public class Column2 {
    private static final char[] CHARS = new char[]{'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', '[', ']', '{', '}', '\\', '|',
            'a', 'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ';', ':', '\'', '\"',
            'z', 'x', 'c', 'v', 'b', 'n', 'm', ',', '<', '.', '>', '/', '?', ' '};

    private int e;
    private long f;
    private double g;
    private String h = "";
    private Date date = new Date();
    private List<Integer> list = new ArrayList<>();
    private Map<String, Object> map = new HashMap<>();

    public Column2() {
        this.e = ThreadLocalRandom.current().nextInt(100);
        this.f = 1000 + ThreadLocalRandom.current().nextLong(10000);
        this.g = 100000 + ThreadLocalRandom.current().nextDouble(10000);

        for (int i = 0; i < 5 + ThreadLocalRandom.current().nextInt(20); i++) {
            h += CHARS[ThreadLocalRandom.current().nextInt(CHARS.length)];
            map.put(i + "", h);
        }

        for (int i = 0; i < 5 + ThreadLocalRandom.current().nextInt(20); i++) {
            list.add(ThreadLocalRandom.current().nextInt(100000));
            map.put(ThreadLocalRandom.current().nextInt(100000) + "", ThreadLocalRandom.current().nextInt(100000));
        }
    }

    //setter && getter
    public int getE() {
        return e;
    }

    public void setE(int e) {
        this.e = e;
    }

    public long getF() {
        return f;
    }

    public void setF(long f) {
        this.f = f;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
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
        return "Column2{" +
                "e=" + e +
                ", f=" + f +
                ", g=" + g +
                ", h='" + h + '\'' +
                ", date=" + date +
                ", list=" + list +
                ", map=" + map +
                '}';
    }
}
