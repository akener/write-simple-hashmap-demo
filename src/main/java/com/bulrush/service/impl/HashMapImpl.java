package com.bulrush.service.impl;

import com.bulrush.service.HashMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lw on 2019/1/7.
 */
public class HashMapImpl<K,V> implements HashMap<K,V>{

    //定义数组默认大小
    private int defalutLenth = 16;
    //负载因子
    private double defaultAddSizeFactor = 0.75;
    //使用数组位置总和
    private double useSize;
    //定义map骨架数组
    private Entry<K,V>[] table;

    public HashMapImpl(){

        if(defalutLenth<0){
            throw new IllegalArgumentException("数组长度为负数"+defalutLenth);
        }
        if(defaultAddSizeFactor<=0 || Double.isNaN(defaultAddSizeFactor)){
            throw new IllegalArgumentException("扩容标准必须大于0的数字"+defaultAddSizeFactor);
        }
        this.defalutLenth  = defalutLenth;
        this.defaultAddSizeFactor = defaultAddSizeFactor;
        //初始化给与默认长度
        table = new Entry[defalutLenth];
    }

    @Override
    public V put(K k, V v) {
        //如果使用数组大于默认数组大小,则需扩容
        if(useSize>defaultAddSizeFactor*defalutLenth){
            up2Size();
        }
        //通过可以计算出存储位置
        int index = getIndex(k,table.length);

        Entry<K,V> entry =table[index];
        Entry<K,V> newEntry = new Entry<K,V>(k,v,null);
        if(entry==null){
            table[index] = newEntry;
            useSize++;
        }else {
            //维护数组相同位置队列
            Entry newEntryTmp =new Entry(k,v,entry);
            table[index] = newEntryTmp;
        }
        return newEntry.getValue();
    }

    //扩容数组
    private void up2Size() {
        Entry<K,V>[] newTable = new Entry[defalutLenth*2];
        //将原table中的entry重新散列到新的table中
        againHash(newTable);
    }
    //将原table中的entry重新散列到新的table中
    private void againHash(Entry<K,V>[] newTable) {
        //数组里面对象,封装到list中,包括同一位置   有列表结构的都解析出来
        List<Entry<K,V>> entryList = new ArrayList<Entry<K,V>>();
        for(int i=0;i<table.length;i++){
            if(table[1] == null){
                continue;
            }
            findEntryByNext(table[i],entryList);
        }
    }
    private int getIndex(K k, int length) {
        //通常hashCode取膜法
        int m= length-1;
        //& 运算,详解https://blog.csdn.net/AAA821/article/details/73441590
        // 我的理解是,0表示false,1表示true:0100 & 0111 = 0100  ,原因为自由第二位同时都为1
        int index = hash(k.hashCode()) & m;
        return index>=0 ?index : -index;
    }

    //创建hash算法,保证算出的位置值数组中均匀分布,后续补充
    private int hash(int hashCode){
        //java的位运算符,详细介绍https://blog.csdn.net/blog_szhao/article/details/23997881
        hashCode = hashCode>>>3;
        return hashCode;
    }

    private void findEntryByNext(Entry<K,V> entry, List<Entry<K,V>> entryList) {

        if(entry != null && entry.next != null){
            //entry对象形成列表结构
            entryList.add(entry);
            //递归  将列表中的entry实体  都一次封装到entryList链表中
            findEntryByNext(entry.next,entryList);
        }else {
            entryList.add(entry);
        }
    }

    @Override
    public V get(K k) {
        //通过key来计算出存储位置
        int index= getIndex(k,table.length);
        Entry<K,V> entry =table[index];
        if(entry==null){
            throw  new NullPointerException();
        }
        return findEntryByKey(k,entry);
    }

    private V findEntryByKey(K k, Entry<K,V> entry) {
        if(k==entry.getKey() || k.equals(entry.getKey())){
            return  entry.v;
        }else if (entry.next != null){
            return  findEntryByKey(k,entry.next);
        }
        return null;
    }

    class Entry<K,V> implements HashMap.Entry<K,V>{
        K k;
        V v;
        //指向被this挤压下去的entry
        Entry<K,V> next;

        public Entry(K k,V v,Entry<K,V> next){
            this.k = k;
            this.v = v;
            this.next = next;
        }

        @Override
        public K getKey() {
            return k;
        }

        @Override
        public V getValue() {
            return v;
        }
    }


    //测试
    public static void main(String[] args) {
        HashMap<String,String> hashMap = new HashMapImpl<String,String>();
        hashMap.put("aa","bb");
        hashMap.put("aa","cc");
        hashMap.put("aa","dd");
        hashMap.put("aa","ee");

        System.out.println(hashMap.get("aa"));
    }


}
