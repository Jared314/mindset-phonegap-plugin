package com.bluecasedevelopment.mindset;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;
import com.phonegap.api.PluginResult.Status;

public class MindSetPlugin extends Plugin {

	public static final String START_ACTION = "start";
	public static final String STOP_ACTION = "stop";

	private final HashMap<String, MindSetListener> mindsetListeners;

	public MindSetPlugin() {
		this.mindsetListeners = new HashMap<String, MindSetListener>();
	}

	@Override
	public boolean isSynch(String action) {
		return true;
	}

	@Override
	public PluginResult execute(String action, JSONArray args, String callbackId) {
		Log.d("MindSetPlugin", "Plugin Called");
		PluginResult result = null;
		PluginResult.Status status = PluginResult.Status.OK;

		try {
			if (START_ACTION.equals(action)) {
				Log.d("MindSetPlugin", "Start passed");
				String o = start(args.getString(0), args.getBoolean(1), args
						.getInt(2), args.getInt(3), args.getString(4));
				result = new PluginResult(status, o);

			} else if (STOP_ACTION.equals(action)) {
				Log.d("MindSetPlugin", "Stop passed");
				stop(args.getString(0));
				result = new PluginResult(status, "");

			} else {
				result = new PluginResult(Status.INVALID_ACTION);
				Log
						.d("MindSetPlugin", "Invalid action : " + action
								+ " passed");
			}
		} catch (JSONException ex) {
		}
		return result;
	}

	@Override
	public void onDestroy() {
		Set<Entry<String, MindSetListener>> s = this.mindsetListeners
				.entrySet();
		Iterator<Entry<String, MindSetListener>> it = s.iterator();
		while (it.hasNext()) {
			Map.Entry<String, MindSetListener> entry = it.next();
			MindSetListener listener = (MindSetListener) entry.getValue();
			listener.destroy();
		}
		this.mindsetListeners.clear();
	}

	private String start(String key, boolean enableHighAccuracy, int timeout,
			int maximumAge, String deviceAddress) {
		MindSetListener listener = this.mindsetListeners.get(key);
		if (listener == null) {
			listener = new MindSetListener(this, key, timeout, deviceAddress);
			this.mindsetListeners.put(key, listener);
		}

		listener.start(maximumAge);
		return key;
	}

	private void stop(String key) {
		MindSetListener listener = this.mindsetListeners.remove(key);
		if (listener != null)
			listener.stop();
	}
}
