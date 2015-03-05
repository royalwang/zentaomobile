package com.cnezsoft.zentao;

import android.os.AsyncTask;

/**
 * Custom async task
 * Created by sunhao on 15/3/5.
 */
public class CustomAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    public CustomAsyncTask(DoInBackgroundHandler<Params, Result> doInBackgroundHandler,
                           OnPostExecuteHandler<Result> onPostExecuteHandler,
                           OnProgressUpdateHandler<Progress> onProgressUpdateHandler,
                           OnPreExecuteHandler onPreExecuteHandler) {
        this.doInBackgroundHandler = doInBackgroundHandler;
        this.onProgressUpdateHandler = onProgressUpdateHandler;
        this.onPreExecuteHandler = onPreExecuteHandler;
        this.onPostExecuteHandler = onPostExecuteHandler;
    }

    public CustomAsyncTask(DoInBackgroundHandler<Params, Result> doInBackgroundHandler,
                           OnPostExecuteHandler<Result> onPostExecuteHandler,
                           OnProgressUpdateHandler<Progress> onProgressUpdateHandler) {
        this.doInBackgroundHandler = doInBackgroundHandler;
        this.onProgressUpdateHandler = onProgressUpdateHandler;
        this.onPostExecuteHandler = onPostExecuteHandler;
    }

    public CustomAsyncTask(DoInBackgroundHandler<Params, Result> doInBackgroundHandler,
                           OnPostExecuteHandler<Result> onPostExecuteHandler) {
        this.doInBackgroundHandler = doInBackgroundHandler;
        this.onPostExecuteHandler = onPostExecuteHandler;
    }

    public CustomAsyncTask(DoInBackgroundHandler<Params, Result> doInBackgroundHandler) {
        this.doInBackgroundHandler = doInBackgroundHandler;
    }

    public interface DoInBackgroundHandler<Params, Result> {
        Result doInBackground(Params... params);
    }

    public interface OnProgressUpdateHandler<Progress> {
        void onProgressUpdate(Progress... values);
    }

    public interface OnPreExecuteHandler{
        boolean onPreExecute();
    }

    public interface OnPostExecuteHandler<Result> {
        void onPostExecute(Result result);
    }

    private DoInBackgroundHandler<Params, Result> doInBackgroundHandler;
    private OnProgressUpdateHandler<Progress> onProgressUpdateHandler;
    private OnPreExecuteHandler onPreExecuteHandler;
    private OnPostExecuteHandler<Result> onPostExecuteHandler;

    @Override
    protected Result doInBackground(Params... params) {
        if(doInBackgroundHandler != null) {
            return doInBackgroundHandler.doInBackground(params);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Progress... values) {
        if(onProgressUpdateHandler != null) {
            onProgressUpdateHandler.onProgressUpdate(values);
        }
    }

    @Override
    protected void onPreExecute() {
        if(onPreExecuteHandler != null) {
            if(onPreExecuteHandler.onPreExecute()) {
                cancel(true);
            }
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        if(onPostExecuteHandler != null) {
            onPostExecuteHandler.onPostExecute(result);
        }
    }


}
