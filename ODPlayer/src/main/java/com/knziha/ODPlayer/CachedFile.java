package com.knziha.ODPlayer;

import java.io.File;
import java.net.URI;

public class CachedFile extends File {
    public CachedFile(String pathname) {
        super(pathname);
    }

    public CachedFile(String parent, String child) {
        super(parent, child);
    }

    public CachedFile(File parent, String child) {
        super(parent, child);
    }

    public CachedFile(URI uri) {
        super(uri);
    }
    public long mLastModified=-1;
    public long mLength=-1;
    @Override
    public long lastModified() {
        return mLastModified==-1?mLastModified=super.lastModified():mLastModified;
    }

    @Override
    public long length() {
        return mLength==-1?mLength=super.length():mLength;
    }
}
