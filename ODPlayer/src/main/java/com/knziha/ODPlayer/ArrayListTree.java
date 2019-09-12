package com.knziha.ODPlayer;

import java.util.ArrayList;
import java.util.Comparator;


public class ArrayListTree<T1 extends Comparable<?>> {
	//greate and wonderful and splendid and glory!
	boolean bIsDuplicative= true;
	public final ArrayList<T1> data;
	public Comparator<T1> mComparator;


	public ArrayListTree(){
		data = new  ArrayList<>();
	}
	public ArrayListTree(int initialCap){
		data = new  ArrayList<>(initialCap);
	}
    public ArrayListTree(int initialCap, Comparator<T1> mComparator_){
        data = new  ArrayList<>(initialCap);
        mComparator=mComparator_;
    }
	
	public int insert(T1 val){
		int idx = binarySearch(data,val,mComparator);

		if(idx==-1)
			return idx;
		if(idx<0) {
			idx = -2-idx;
			if(idx>=0) {
				
			}else
				return -1;
		}else{
		    if(!bIsDuplicative)
		        return -1;
        }

		data.add(idx,val);
		return idx;
	}

    private int compareTo(T1 t1, T1 t2) {
	    if(mComparator!=null)
            return mComparator.compare(t1,t2);
	    else
	        return ((Comparable)t1).compareTo(t2);
    }

//    @SuppressWarnings("rawtypes")
//    private static int binarySearch(ArrayList a,Object key) {
//			int low = 0;
//			int high = a.size() - 1;
//
//			while (low <= high) {
//				int mid = (low + high) >>> 1;
//				Comparable middle = (Comparable) a.get(mid);
//				int cmp = middle.compareTo(key);
//
//				if (cmp < 0)
//					low = mid + 1;
//				else if (cmp > 0)
//					high = mid - 1;
//				else
//					return mid; // key found
//			}
//			return -(low+2);  // key not found.
//	}
	
	public int lookUpKey(T1 key, boolean isStrict) {
	    //CMN.Log("asdasd",data.size());
		int ret = binarySearch(data,key,mComparator);
		if(isStrict)
			return ret;
		else {
			if(ret<-1)
				return -2-ret;
			return ret;
		}
	}

    public static <T extends  Comparable<T>> int lookUpKey(ArrayList<T> data, T key,Comparator<T> cpr, boolean isStrict) {
        int ret = binarySearch(data,key,cpr);
        if(isStrict)
            return ret;
        else {
            if(ret<-1)
                return -2-ret;
            return ret;
        }
    }

    @SuppressWarnings("rawtypes")
    public static  <T extends  Comparable<?>>  int binarySearch(ArrayList<T> a,T key,Comparator<T> cpr) {
        int low = 0;
        int high = a.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = cpr!=null?
                    cpr.compare(a.get(mid), key):
                    ((Comparable)a.get(mid)).compareTo(key);
            //CMN.Log(low,mid,high,a.get(mid));
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low+2);  // key not found.
    }

	public Integer size() {
		return data.size();
	}

	public void add(T1 val) {
		data.add(val);
	}


    public T1 getKeyAt(int pos) {
	    return data.get(pos);
    }
}
