package com.knziha.filepicker.model;

import android.media.MediaMetadataRetriever;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class AudioCoverFetcher implements DataFetcher<InputStream> {

	private final AudioCover model;
	private MediaMetadataRetriever mRetriever;

	public AudioCoverFetcher(AudioCover model) {
		this.model = model;
	}

	public AudioCover getModel() {
		return model;
	}

	@Override
	public void loadData(@NonNull Priority priority, @NonNull DataFetcher.DataCallback<? super InputStream> callback) {
		mRetriever = new MediaMetadataRetriever();
		try {
			mRetriever.setDataSource(model.path);
			byte[] picture = mRetriever.getEmbeddedPicture();
			if (picture != null) {
				callback.onDataReady(new ByteArrayInputStream(picture));
			} else {
				callback.onLoadFailed(new Exception("load audio cover fail"));
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override public void cleanup() {
		mRetriever.release();
	}
	@Override public void cancel() {
		// cannot cancel
	}

	@NonNull
	@Override
	public Class<InputStream> getDataClass() {
		return InputStream.class;
	}

	@NonNull
	@Override
	public DataSource getDataSource() {
		return DataSource.LOCAL;
	}
}