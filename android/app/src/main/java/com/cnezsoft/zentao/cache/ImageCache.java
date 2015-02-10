package com.cnezsoft.zentao.cache;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.LruCache;

import com.cnezsoft.zentao.ZentaoAPI;
import com.cnezsoft.zentao.ZentaoApplication;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Catouse on 2015/2/9.
 */
public class ImageCache {

    /**
     * Convert url to filepath
     * @param url
     * @return
     */
    private static String convertUrlToFilePath(String url) {
        int index = url.lastIndexOf('.');

        StringBuffer filePath = new StringBuffer();

        filePath.append(application.getCacheDir().toString()).append("/");

        filePath.append(ZentaoAPI.md5(url));
        if(index > -1) {
            filePath.append(url.substring(index));
        }
        return filePath.toString();
    }

    public class ImageRef {

        private String url;
        private String path;
        private Bitmap bitmap;

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public String getUrl() {
            return url;
        }

        public String getPath() {
            return path;
        }

        public void setUrl(String url) {
            this.url = url;
            this.path = convertUrlToFilePath(url);
        }

        public ImageRef(String url) {
            setUrl(url);
        }
    }

    public interface OnImageReadyListener {
        void onImageReady(ImageRef ref);
    }

    private static ImageCache imageCache;
    private static final int DISK_CACHE_SIZE = 1024*1024*20; // 10MB
    private static final String DISK_CACHE_SUB_DIR = "images";
    private static ZentaoApplication application;

    public static ImageCache from(Context context) {
        if(Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("Cannot instantiate outside UI thread.");
        }

        if(application == null) {
            application = (ZentaoApplication) context.getApplicationContext();
        }

        if(imageCache == null) {
            imageCache = new ImageCache(application);
        }

        return imageCache;
    }

    private DiskLruCache diskCache;
    private LruCache<String, Bitmap> memoryCache;

    /**
     * Private construtor ensure the singlaton mode
     * @param context
     */
    private ImageCache(Context context) {
        int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        memClass = memClass > 32 ? 32 : memClass;

        final int cacheSize = 1024 * 1024 * memClass / 8;
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };

        File cacheDir = DiskLruCache.getDiskCacheDir(context, DISK_CACHE_SUB_DIR);
        diskCache = DiskLruCache.openCache(context, cacheDir, DISK_CACHE_SIZE);
    }

    public Bitmap getFromMemory(String url) {
        ImageRef ref = new ImageRef(url);
        return memoryCache.get(ref.getPath());
    }

    public void imgReady(String[] urls, OnImageReadyListener listener) {
        ArrayList<ImageRef> imgRefSet = new ArrayList<>();
        for(String url: urls) {
            ImageRef ref = new ImageRef(url);
            Bitmap bitmap = memoryCache.get(ref.getPath());
            if(bitmap != null) {
                ref.setBitmap(bitmap);
                listener.onImageReady(ref);
            } else {
                imgRefSet.add(ref);
            }
        }
        if(imgRefSet.size() > 0) {
            new ImageLoader(listener).execute(imgRefSet.toArray(new ImageRef[imgRefSet.size()]));
        }
    }

    /**
     * Load byte array from network
     * @param url
     * @return
     */
    private Bitmap loadImageFromNetwork(String url) {
        try {
            InputStream is = new DefaultHttpClient().execute(new HttpGet(url))
                    .getEntity().getContent();
            return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            return null;
        }
    }

    private class ImageLoader extends AsyncTask<ImageRef, ImageRef, Integer> {
        private OnImageReadyListener onImageReadyListener;

        private ImageLoader(OnImageReadyListener listener) {
            onImageReadyListener = listener;
        }

        @Override
        protected Integer doInBackground(ImageRef... refs) {
            int success = 0;
            for(ImageRef ref: refs) {
                Bitmap bitmap = diskCache.get(ref.getPath());
                if(bitmap == null) {
                    bitmap = loadImageFromNetwork(ref.getUrl());
                    if(bitmap != null) {
                        diskCache.put(ref.getPath(), bitmap);
                    }
                }

                if(bitmap != null) {
                    memoryCache.put(ref.getPath(), bitmap);
                    ref.setBitmap(bitmap);
                    onProgressUpdate(ref);
                }
            }
            return success;
        }

        /**
         * Runs on the UI thread after {@link #publishProgress} is invoked.
         * The specified values are the values passed to {@link #publishProgress}.
         *
         * @param values The values indicating progress.
         * @see #publishProgress
         * @see #doInBackground
         */
        @Override
        protected void onProgressUpdate(ImageRef... refs) {
            super.onProgressUpdate(refs);
            if(onImageReadyListener != null) {
                for(ImageRef ref: refs) {
                    onImageReadyListener.onImageReady(ref);
                }
            }
        }
    }
}
