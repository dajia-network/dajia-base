package com.dajia.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huhaonan on 2016/10/26.
 */
public class SimpleMap<K,V> {

    public static HashMap build(Object ... kvs) {
        HashMap map = new HashMap();
        int k = kvs.length;
        if(k == 0) {
            return map;
        }

        if(k%2 != 0) {
            throw new IllegalArgumentException("构造simple的参数个数必须是偶数");
        }

        int count = 0;
        int last = kvs.length - 1;
        while(count < last - 1) {
            map.put(kvs[count++], kvs[count++]);
        }
        return map;
    }

    public static void main(String[] args) {
        HashMap map = build("Name", "huhaonan", "Age", "0");
        System.out.println(map);
    }

}
