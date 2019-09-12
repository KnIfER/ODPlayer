package com.knziha.filepicker.model;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.knziha.filepicker.view.CMNF;

import java.io.File;
import java.io.InputStream;

@GlideModule
public class GlideCacheModule extends AppGlideModule {
    public static String path = "/storage/emulated/0/PLOD/thm";
    public static boolean bUseLruDiskCache;
	@Override
	public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
		super.registerComponents(context, glide, registry);
		registry.append( AudioCover.class, InputStream.class, new AudioCoverLoaderFactory());
	}

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
        //Log.e("applyOptions" ,bUseLruDiskCache+" : "+path );
        builder.setDiskCache(bUseLruDiskCache?
                new GoodDiskCacheFactory(DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE)://300*1024 DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE
                new GoodDiskCacheFactoryForever());
        builder.setLogLevel(Log.ERROR);
    }

    class GoodDiskCacheFactory extends DiskLruCacheFactory {
        public GoodDiskCacheFactory(long diskCacheSize) {
            super(() -> new File(path), diskCacheSize);
        }
    }

    class GoodDiskCacheFactoryForever implements  DiskCache.Factory{
        public GoodDiskCacheFactoryForever() {}
        @Override public DiskCache build() {
            return new SimpleDiskCache(path);
        }
    }

}
