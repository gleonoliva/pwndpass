package android.os;

/*
* This is a mock of AsyncTask that removes the asynchronous part and instead runs synchronously
* just for tests. Credit to Ryan Harter:
* https://ryanharter.com/blog/2015/12/28/dealing-with-asynctask-in-unit-tests/
* */
public abstract class AsyncTask<Params, Progress, Result> {

    protected abstract Result doInBackground(Params... params);

    protected void onPostExecute(Result result) {

    }

    protected void onProgressUpdate(Progress... values) {

    }

    public AsyncTask<Params, Progress, Result> execute(Params... params) {
        Result result = doInBackground(params);
        onPostExecute(result);
        return this;
    }
}
