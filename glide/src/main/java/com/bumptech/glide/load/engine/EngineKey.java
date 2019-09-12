package com.bumptech.glide.load.engine;

import android.util.Log;

import androidx.annotation.NonNull;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;
import java.util.Map;

/** An in memory only cache key used to multiplex loads. */
public class EngineKey implements Key {
  public final Object model;
  public final int width;
  public final int height;
  public final Class<?> resourceClass;
  public final Class<?> transcodeClass;
  public final Key signature;
  public final Map<Class<?>, Transformation<?>> transformations;
  public final Options options;
  public int hashCode;

  EngineKey(
      Object model,
      Key signature,
      int width,
      int height,
      Map<Class<?>, Transformation<?>> transformations,
      Class<?> resourceClass,
      Class<?> transcodeClass,
      Options options) {
    this.model = Preconditions.checkNotNull(model);
    //Log.e("fatal posision", "model:enginekey"+model);
    this.signature = Preconditions.checkNotNull(signature, "Signature must not be null");
    this.width = width;
    this.height = height;
    this.transformations = Preconditions.checkNotNull(transformations);
    this.resourceClass =
        Preconditions.checkNotNull(resourceClass, "Resource class must not be null");
    this.transcodeClass =
        Preconditions.checkNotNull(transcodeClass, "Transcode class must not be null");
    this.options = Preconditions.checkNotNull(options);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof EngineKey) {
      EngineKey other = (EngineKey) o;
      return model.equals(other.model)
          && signature.equals(other.signature)
          && height == other.height
          && width == other.width
          && transformations.equals(other.transformations)
          && resourceClass.equals(other.resourceClass)
          && transcodeClass.equals(other.transcodeClass)
          && options.equals(other.options);
    }
    return false;
  }

  @Override
  public int hashCode() {
    if (hashCode == 0) {
      hashCode = model.hashCode();
      hashCode = 31 * hashCode + signature.hashCode();
      hashCode = 31 * hashCode + width;
      hashCode = 31 * hashCode + height;
      hashCode = 31 * hashCode + transformations.hashCode();
      hashCode = 31 * hashCode + resourceClass.hashCode();
      hashCode = 31 * hashCode + transcodeClass.hashCode();
      hashCode = 31 * hashCode + options.hashCode();
    }
    return hashCode;
  }

  public String hashCodes() {
      String ret = "";
      ret += model.hashCode();ret+=" , ";
      ret += signature.hashCode();ret+=" , ";
      ret += width;ret+=" , ";
      ret += height;ret+=" , ";
      ret += transformations.hashCode();ret+=" , ";
      ret += resourceClass.hashCode();ret+=" , ";
      ret += transcodeClass.hashCode();ret+=" , ";
      ret += options.hashCode();ret+=" , ";
      return ret;
  }

  @Override
  public String toString() {
    return "EngineKey{"
        + "model="
        + model
        + ", width="
        + width
        + ", height="
        + height
        + ", resourceClass="
        + resourceClass
        + ", transcodeClass="
        + transcodeClass
        + ", signature="
        + signature
        + ", hashCode="
        + hashCode
        + ", transformations="
        + transformations
        + ", options="
        + options
        + '}';
  }

  @Override
  public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
    throw new UnsupportedOperationException();
  }
}
