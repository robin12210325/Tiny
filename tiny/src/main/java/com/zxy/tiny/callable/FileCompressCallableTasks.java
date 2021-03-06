package com.zxy.tiny.callable;

import android.graphics.Bitmap;
import android.net.Uri;

import com.zxy.tiny.Tiny;
import com.zxy.tiny.common.BatchCompressResult;
import com.zxy.tiny.common.CompressResult;
import com.zxy.tiny.common.TinyException;
import com.zxy.tiny.core.BitmapCompressor;
import com.zxy.tiny.core.CompressKit;
import com.zxy.tiny.core.FileCompressor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.zxy.tiny.core.BitmapCompressor.compress;

/**
 * Created by zhengxiaoyong on 2017/3/13.
 */
public class FileCompressCallableTasks {

    private FileCompressCallableTasks() {
        throw new TinyException.UnsupportedOperationException("can not be a instance");
    }

    public static final class InputStreamAsFileCallable extends BaseFileCompressCallable {
        private InputStream mInputStream;

        public InputStreamAsFileCallable(Tiny.FileCompressOptions options, boolean withBitmap, InputStream is) {
            super(options, withBitmap);
            mInputStream = is;
        }

        @Override
        public CompressResult call() throws Exception {
            return FileCompressor.compress(CompressKit.transformToByteArray(mInputStream), mCompressOptions, shouldReturnBitmap, true);
        }
    }

    public static final class BitmapAsFileCallable extends BaseFileCompressCallable {
        private Bitmap mBitmap;

        public BitmapAsFileCallable(Tiny.FileCompressOptions options, boolean withBitmap, Bitmap bitmap) {
            super(options, withBitmap);
            mBitmap = bitmap;
        }

        @Override
        public CompressResult call() throws Exception {
            Bitmap bitmap = BitmapCompressor.compress(mBitmap, mCompressOptions, false);
            return FileCompressor.compress(bitmap, mCompressOptions, shouldReturnBitmap, false);
        }
    }

    public static final class FileAsFileCallable extends BaseFileCompressCallable {
        private File mFile;

        public FileAsFileCallable(Tiny.FileCompressOptions options, boolean withBitmap, File file) {
            super(options, withBitmap);
            mFile = file;
        }

        @Override
        public CompressResult call() throws Exception {
            CompressResult result = null;
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(mFile);
                result = FileCompressor.compress(CompressKit.transformToByteArray(fis), mCompressOptions, shouldReturnBitmap, true);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null)
                        fis.close();
                } catch (IOException e) {
                    //ignore...
                }
            }
            return result;
        }
    }

    public static final class UriAsFileCallable extends BaseFileCompressCallable {
        private Uri mUri;

        public UriAsFileCallable(Tiny.FileCompressOptions options, boolean withBitmap, Uri uri) {
            super(options, withBitmap);
            mUri = uri;
        }

        @Override
        public CompressResult call() throws Exception {
            Bitmap bitmap = new BitmapCompressCallableTasks.UriAsBitmapCallable(mCompressOptions, mUri).call();
            return FileCompressor.compress(bitmap, mCompressOptions, shouldReturnBitmap, true);
        }
    }

    public static final class ByteArrayAsFileCallable extends BaseFileCompressCallable {
        private byte[] mBytes;

        public ByteArrayAsFileCallable(Tiny.FileCompressOptions options, boolean withBitmap, byte[] bytes) {
            super(options, withBitmap);
            mBytes = bytes;
        }

        @Override
        public CompressResult call() throws Exception {
            return FileCompressor.compress(mBytes, mCompressOptions, shouldReturnBitmap, true);
        }
    }

    public static final class ResourceAsFileCallable extends BaseFileCompressCallable {
        private int mResId;

        public ResourceAsFileCallable(Tiny.FileCompressOptions options, boolean withBitmap, int resId) {
            super(options, withBitmap);
            mResId = resId;
        }

        @Override
        public CompressResult call() throws Exception {
            Bitmap bitmap = compress(mResId, mCompressOptions, false);
            return FileCompressor.compress(bitmap, mCompressOptions, shouldReturnBitmap, true);
        }
    }

    public static final class FileArrayAsFileCallable extends BaseFileBatchCompressCallable {
        private File[] mFiles;

        public FileArrayAsFileCallable(Tiny.FileCompressOptions options, boolean withBitmap, File[] files) {
            super(options, withBitmap);
            mFiles = files;
        }

        @Override
        public BatchCompressResult call() throws Exception {
            if (mFiles == null)
                return null;
            BatchCompressResult result = new BatchCompressResult();
            result.results = new CompressResult[mFiles.length];

            for (int i = 0; i < mFiles.length; i++) {
                File file = mFiles[i];
                if (file == null) {
                    result.results[i] = null;
                    continue;
                }
                CompressResult compressResult = null;
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    compressResult = FileCompressor.compress(CompressKit.transformToByteArray(fis), mCompressOptions, shouldReturnBitmap, true);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fis != null)
                            fis.close();
                    } catch (IOException e) {
                        //ignore...
                    }
                }
                if (compressResult != null)
                    result.success = true;
                result.results[i] = compressResult;
            }

            return result;
        }
    }

    public static final class BitmapArrayAsFileCallable extends BaseFileBatchCompressCallable {
        private Bitmap[] mBitmaps;

        public BitmapArrayAsFileCallable(Tiny.FileCompressOptions options, boolean withBitmap, Bitmap[] bitmaps) {
            super(options, withBitmap);
            mBitmaps = bitmaps;
        }

        @Override
        public BatchCompressResult call() throws Exception {
            if (mBitmaps == null)
                return null;
            BatchCompressResult result = new BatchCompressResult();
            result.results = new CompressResult[mBitmaps.length];

            for (int i = 0; i < mBitmaps.length; i++) {
                Bitmap bitmap = mBitmaps[i];
                CompressResult compressResult = FileCompressor.compress(bitmap, mCompressOptions, shouldReturnBitmap, false);
                if (compressResult != null)
                    result.success = true;
                result.results[i] = compressResult;
            }
            return result;
        }
    }

    public static final class UriArrayAsFileCallable extends BaseFileBatchCompressCallable {
        private Uri[] mUris;

        public UriArrayAsFileCallable(Tiny.FileCompressOptions options, boolean withBitmap, Uri[] uris) {
            super(options, withBitmap);
            mUris = uris;
        }

        @Override
        public BatchCompressResult call() throws Exception {
            if (mUris == null)
                return null;
            BatchCompressResult result = new BatchCompressResult();
            result.results = new CompressResult[mUris.length];

            for (int i = 0; i < mUris.length; i++) {
                Uri uri = mUris[i];
                if (uri == null) {
                    result.results[i] = null;
                    continue;
                }
                CompressResult compressResult = new UriAsFileCallable(mCompressOptions, shouldReturnBitmap, uri).call();
                if (compressResult != null)
                    result.success = true;
                result.results[i] = compressResult;
            }

            return result;
        }
    }

    public static final class ResourceArrayAsFileCallable extends BaseFileBatchCompressCallable {
        private int[] mResIds;

        public ResourceArrayAsFileCallable(Tiny.FileCompressOptions options, boolean withBitmap, int[] resIds) {
            super(options, withBitmap);
            mResIds = resIds;
        }

        @Override
        public BatchCompressResult call() throws Exception {
            if (mResIds == null)
                return null;
            BatchCompressResult result = new BatchCompressResult();
            result.results = new CompressResult[mResIds.length];

            for (int i = 0; i < mResIds.length; i++) {
                Bitmap bitmap = compress(mResIds[i], mCompressOptions, false);
                CompressResult compressResult = FileCompressor.compress(bitmap, mCompressOptions, shouldReturnBitmap, true);
                if (compressResult != null)
                    result.success = true;
                result.results[i] = compressResult;
            }
            return result;
        }
    }

}
