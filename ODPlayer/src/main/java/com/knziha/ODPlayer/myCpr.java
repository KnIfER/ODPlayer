package com.knziha.ODPlayer;
public class myCpr implements Comparable<myCpr>{
    public int key;
    public int value;
    public myCpr(int k, int v){
        key=k;value=v;
    }
    public int compareTo(myCpr other) {
        return this.key-other.key;
    }

    public String toString(){
        return key+" "+value;
    }

}