package com.emarinel.common;

import android.os.AsyncTask;

/**
 * @author emarinelli
 */
public abstract class EugeneAsyncTask<RESULT> extends AsyncTask<Void, Integer, RESULT> {

	private Exception exception;

	protected abstract RESULT doInBackground() throws Exception;

	public EugeneAsyncTask() {
		this.exception = null;
	}

	protected void onSuccess(RESULT result) {
		// Do nothing by default.
	}

	protected void onFinish() {
		// Do nothing by default.
	}

	protected void onFailure(Throwable t) {
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
			this.onFailure(this.exception);
		} else {
			this.onSuccess(result);
		}

		onFinish();
	}

	protected final Exception getException() {
		return this.exception;
	}
}
