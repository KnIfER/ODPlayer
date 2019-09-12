package com.knziha.panorama;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.knziha.panorama.GLUtil.createAndLinkProgram;

/**
 * Created by 海米 on 2017/7/7.
 */

public class ShaderUtils {
    private static final String TAG = "ShaderUtils";

    public static void checkGlError(String label) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, label + ": glError " + error);
            throw new RuntimeException(label + ": glError " + error);
        }
    }

    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShaderHandle == 0) {
            return 0;
        }
        int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragmentShaderHandle == 0) {
            return 0;
        }

        int mProgramHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[]{"aTexCoord", "aTexCoord"});

        return mProgramHandle;
    }


    public static int compileShader(int shaderType, String source) {
        int shaderHandle = GLES20.glCreateShader(shaderType);
        if (shaderHandle != 0) {
            // Pass in the shader source.
            GLES20.glShaderSource(shaderHandle, source);

            // Compile the shader.
            GLES20.glCompileShader(shaderHandle);

            // Get the compilation status.
            int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0) {
                Log.e("fatal", "Could not compile shader " + shaderType + ":" + GLES20.glGetShaderInfoLog(shaderHandle));
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }

        if (shaderHandle == 0) {
            //throw new RuntimeException("Error creating shader.");
        }

        return shaderHandle;
    }

    public static String readRawTextFile(Context context, int resId) {
        InputStream inputStream = context.getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
