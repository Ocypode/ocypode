package com.ocypode.volleyrestclient.infrastructure.request;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocypode.volleyrestclient.infrastructure.cache.BufferedDiskBasedCache;
import com.ocypode.volleyrestclient.utility.bitmap.BitmapUtil;

/**
 * If we start having performance problems: http://stackoverflow.com/questions/20916478/performance-issue-with-volleys-diskbasedcache
 * This class is @Singleton, be aware when you use {@link newImageLoaderInMemory}
 * @author jairobjunior@gmail.com
 */
@Singleton
public class ImageLoaderVolley {

	/** Default maximum disk usage in bytes. 10MB */
    private static final int DEFAULT_DISK_USAGE_BYTES = 10 * 1024 * 1024;
    
	@Inject
	protected Context mContext;
	
	@Inject
	private RequestQueueVolley mRequestQueueVolley;
	
	/**
	 * Memorycache is always faster than DiskCache. Check it our for yourself. {@link #newImageLoaderInMemory()}
	 * @return
	 */
	public ImageLoader newImageLoaderInDisk() {
		return new ImageLoader(mRequestQueueVolley.getRequestQueue(),
				new DiskBitmapCache(mContext.getCacheDir(), DEFAULT_DISK_USAGE_BYTES));
	}
	
	public ImageLoader newImageLoaderInMemory() {
		return new ImageLoader(mRequestQueueVolley.getRequestQueue(),
				new BitmapLruCache());
	}
	
	public static class DiskBitmapCache extends BufferedDiskBasedCache implements ImageCache {
		 
	    public DiskBitmapCache(File rootDirectory, int maxCacheSizeInBytes) {
	        super(rootDirectory, maxCacheSizeInBytes);
	    }
	 
	    public DiskBitmapCache(File cacheDir) {
	        super(cacheDir);
	    }
	 
	    public Bitmap getBitmap(String url) {
	        final Entry requestedItem = get(url);
	 
	        if (requestedItem == null)
	            return null;
	 
	        return BitmapFactory.decodeByteArray(requestedItem.data, 0, requestedItem.data.length);
	    }
	 
	    public void putBitmap(String url, Bitmap bitmap) {
	    	final Entry entry = new Entry();
	        
/*			//Down size the bitmap.If not done, OutofMemoryError occurs while decoding large bitmaps.
 			// If w & h is set during image request ( using ImageLoader ) then this is not required.
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Bitmap downSized = BitmapUtil.downSizeBitmap(bitmap, 50);
			
			downSized.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] data = baos.toByteArray();
			
			System.out.println("####### Size of bitmap is ######### "+url+" : "+data.length);
	        entry.data = data ; */
			
	        entry.data = BitmapUtil.convertBitmapToBytes(bitmap) ;
	        put(url, entry);
	    }
	}
	
	public static class BitmapLruCache extends LruCache<String, Bitmap> implements ImageCache {
	    
		public static int getDefaultLruCacheSize() {
	        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	        final int cacheSize = maxMemory / 8;

	        return cacheSize;
	    }

	    public BitmapLruCache() {
	        this(getDefaultLruCacheSize());
	    }

	    public BitmapLruCache(int sizeInKiloBytes) {
	        super(sizeInKiloBytes);
	    }

	    @Override
	    protected int sizeOf(String key, Bitmap value) {
	        return value.getRowBytes() * value.getHeight() / 1024;
	    }

	    @Override
	    public Bitmap getBitmap(String url) {
	        return get(url);
	    }

	    @Override
	    public void putBitmap(String url, Bitmap bitmap) {
	        put(url, bitmap);
	    }
	}
	
	
}
