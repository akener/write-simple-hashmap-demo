package com.bulrush.service;

/**
 * Created by lw on 2019/1/7.
 */
public interface HashMap<K,V> {

    public V put(K k,V v);
    public V get(K k);

    public interface Entry<K,V>{
        public K getKey();
        public V getValue();
    }
}
