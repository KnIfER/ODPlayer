package com.bumptech.glide.load.engine;

import android.util.Log;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import java.util.Map;

class EngineKeyFactory {

  @SuppressWarnings("rawtypes")
  EngineKey buildKey(
      Object model,
      Key signature,
      int width,
      int height,
      Map<Class<?>, Transformation<?>> transformations,
      Class<?> resourceClass,
      Class<?> transcodeClass,
      Options options) {
      Object ModifiedModel = model;
    return new EngineKey(
            ModifiedModel, signature, width, height, transformations, resourceClass, transcodeClass, options);
  }
}
