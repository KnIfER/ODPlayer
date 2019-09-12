package com.knziha.filepicker.model;

import java.util.ArrayList;
import java.util.HashSet;

public class ArrayListTree<T extends Comparable<? super T>> {
	//wonderful!
	
	protected final ArrayList<T> data;
	boolean isdirty=false;
	
	public ArrayListTree(){
		data = new  ArrayList<>();
	}
	
	public int insert(T val){
		if(data.size()==0 || data.get(data.size()-1).compareTo(val)<0) {//!!!不允许重�?
			data.add(data.size(),val);
			return data.size();
		}
		int idx = reduce(val,0,data.size());
		if(val.compareTo(data.get(idx))==0) {//不允许重
			isdirty=true;
			return -1;
		}
		data.add(idx,val);
		return idx;
	}
	
	public int reduce(T val,int start,int end) {//via mdict-js
        int len = end-start;
        if (len > 1) {
          len = len >> 1;
          return val.compareTo(data.get(start + len - 1))>0
                    ? reduce(val,start+len,end)
                    : reduce(val,start,start+len);
        } else {
          return start;
        }
    }
	

	public int getCountOf(T key) {
		if(data.size()==0 || data.get(data.size()-1).compareTo(key)<0) {
			return 0;
		}
		int idx = reduce(key,0,data.size());
		int cc=0;
		if(key.compareTo(data.get(idx))==0) {
			cc++;
			while(idx<data.size()-1 && key.compareTo(data.get(idx+1))==0) {
				idx++;cc++;
			}
		}
		return cc;
	}

	public Integer size() {
		return data.size();
	}

	public void add(T val) {
		data.add(val);
	}

	public ArrayList<T> getList() {
		return data;
	}
	
	public final HashSet<T> OverFlow = new HashSet<>();
	public void insertOverFlow(T val) {
		if(OverFlow.contains(val)) {
			insert(val);
		}else
			OverFlow.add(val);
	}

	public boolean contains(T val) {
		if(data.size()==0) return false;
		int idx = reduce(val,0,data.size());
		if(idx==-1) return false;
		if(val.compareTo(data.get(idx))==0) {
			return true;
		}
		return false;
	}

	public T get(T val) {
		if(data.size()==0) return null;
		int idx = reduce(val,0,data.size());
		if(idx==-1) return null;
		if(val.compareTo(data.get(idx))==0) {
			return data.get(idx);
		}
		return null;
	}

	public int indexOf(T val) {
		if(data.size()==0) return -1;
		return reduce(val,0,data.size());
	}

	public int remove(T val) {
		if(data.size()==0) return -1;
		int idx = reduce(val,0,data.size());
		if(idx==-1) return -1;
		if(val.compareTo(data.get(idx))==0) {
			data.remove(idx);
			return idx;
		}
		return -1;
	}

	public void clear() {
		data.clear();
	}
	
	
	
}
