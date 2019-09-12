package com.knziha.equalizer;
public class myCpr implements Comparable<myCpr> {
    public long key;
    public String value;
    public myCpr(long k, String v){
        key=k;value=v;
    }
    public int compareTo(myCpr other) {
        return (int)(this.key-other.key);
    }

    public String toString(){
        return key+" "+value;
    }

}