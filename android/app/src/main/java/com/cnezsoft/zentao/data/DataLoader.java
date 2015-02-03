package com.cnezsoft.zentao.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;

/**
 * Data loader
 * Created by Catouse on 2015/1/27.
 */
public class DataLoader extends AsyncTaskLoader<Cursor> {

    public interface  OnLoadDataListener {
        Cursor onLoadData(Context context);
    }

    final ForceLoadContentObserver observer;

    Cursor mCursor;
    CancellationSignal mCancellationSignal;
    OnLoadDataListener onLoadDataListener;
    EntryType entryType;

    public DataLoader(Context context, EntryType entryType, OnLoadDataListener onLoadDataListener) {
        super(context);
        observer = new ForceLoadContentObserver();
        this.entryType = entryType;
        this.onLoadDataListener = onLoadDataListener;
    }

    /* Runs on a worker thread */
    @Override
    public Cursor loadInBackground() {
        synchronized (this) {
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }
            mCancellationSignal = new CancellationSignal();
        }

        if(onLoadDataListener == null) {
            throw new NullPointerException("onLoadDataListener must not be null.");
        }

        try {
            Cursor cursor = onLoadDataListener.onLoadData(getContext());

            if (cursor != null) {
                try {
                    // Ensure the cursor window is filled.
                    cursor.getCount();
                    cursor.registerContentObserver(observer);
                    cursor.setNotificationUri(getContext().getContentResolver(), DAO.getUri(entryType));
                } catch (RuntimeException ex) {
                    cursor.close();
                    throw ex;
                }
            }
            return cursor;
        } finally {
            synchronized (this) {
                mCancellationSignal = null;
            }
        }
    }

    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();

        synchronized (this) {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }
    }

    /* Runs on the UI thread */
    @Override
    public void deliverResult(Cursor cursor) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (cursor != null) {
                cursor.close();
            }
            return;
        }
        Cursor oldCursor = mCursor;
        mCursor = cursor;

        if (isStarted()) {
            super.deliverResult(cursor);
        }

        if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    /**
     * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks
     * will be called on the UI thread. If a previous load has been completed and is still valid
     * the result may be passed to the callbacks immediately.
     *
     * Must be called from the UI thread
     */
    @Override
    protected void onStartLoading() {
        if (mCursor != null) {
            deliverResult(mCursor);
        }
        if (takeContentChanged() || mCursor == null) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        mCursor = null;
    }
}
