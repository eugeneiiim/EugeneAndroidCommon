package com.emarinel.common;

import android.content.Context;
import android.os.AsyncTask;

/**
 * @author emarinelli
 */
public abstract class EugeneAsyncTask<RESULT> extends AsyncTask<Void, Integer, RESULT> {

	private Exception exception;
	private final Context context; // TODO storing this is dangerous.

	protected abstract RESULT doInBackground() throws Exception;
	protected abstract void onSuccess(RESULT result);

	public EugeneAsyncTask(Context context) {
		ParamUtils.checkNotNull(context, "context");

		this.exception = null;
		this.context = context;
	}

	protected void onFinish() {
		// Do nothing by default.
	}

	protected void requireLogin(Context context) {
		// Do nothing by default.
	}

	@Override
	protected final RESULT doInBackground(Void... arg0) {
		try {
			return this.doInBackground();
		} catch (Exception e) {
			this.exception = e;
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected final void onPostExecute(final RESULT result) {
		if (this.exception != null) {
			onFinish();

			if (this.exception instanceof NotLoggedInException) {
				requireLogin(context);
			} else {
				//Utils.handleConnectionFailure(context);
			}
		} else {
			this.onSuccess(result);
			onFinish();
		}
	}

	protected final Exception getException() {
		return this.exception;
	}
}
