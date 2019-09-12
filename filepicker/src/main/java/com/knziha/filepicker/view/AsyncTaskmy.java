package com.knziha.filepicker.view;

import android.os.AsyncTask;

class AsyncTaskmy<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    @Override
    protected Result doInBackground(Params... params) {
        return null;
    }


    public Result doInBackgroundmy(Params... params){
        return doInBackground(params);
    }
}
