package com.knziha.filepicker.model;

import android.os.Environment;
import android.util.Log;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.EngineKey;
import com.bumptech.glide.load.engine.ResourceCacheKey;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskCacheWriteLocker;
import com.bumptech.glide.load.engine.cache.SafeKeyGenerator;
import com.knziha.filepicker.view.CMNF;

import java.io.File;
import java.io.IOException;

import mp4meta.utils.CMN;

public class SimpleDiskCache implements DiskCache {
    private final String path;
    private final SafeKeyGenerator safeKeyGenerator;
    private final DiskCacheWriteLocker writeLocker = new DiskCacheWriteLocker();

    public SimpleDiskCache(String _path) {
        CMNF.Log("SimpleDiskCacheSimpleDiskCache");
        path=_path;
        safeKeyGenerator = new SafeKeyGenerator();
    }

    @Override
    public File get(Key key) {
        //new ResourceCacheKey(ek.model);
        String safeKey = safeKeyGenerator.getSafeKey(key);
        //CMNF.Log("keykeykey",safeKey+" "+key.getClass());
        //CMNF.Log("keykeykey",safeKey+"  "+rck.sourceKey+"  "+rck.toString());
        File cf = new File(path,safeKey+".0");
        return cf.exists()?cf:null;
    }

    @Override
    public void put(Key key, Writer writer) {
        String safeKey = safeKeyGenerator.getSafeKey(key);
        writeLocker.acquire(safeKey);
        try {
            File file = new File(path,safeKey+".0");
            if (file.exists()) {
                ResourceCacheKey rck = (ResourceCacheKey) key;
                //CMNF.Log("current ",safeKey+"  "+rck.sourceKey+"  "+rck.toString());
                //Log.e("fatal","current "+safeKey+" "+key.getClass());
                return;
            }
            //Log.e("fatal","current writing new");

            writer.write(file);
        }finally {
            writeLocker.release(safeKey);
        }
    }

    @Override
    public void deleteCacheByKey(Key key) {
        Log.e("fatal","deleteCacheByKey deleteCacheByKey deleteCacheByKey");
    }

    @Override
    public void clear() {
        Log.e("fatal","clear clear clear");
    }
}
