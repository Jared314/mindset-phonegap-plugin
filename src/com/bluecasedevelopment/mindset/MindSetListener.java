package com.bluecasedevelopment.mindset;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;

import com.neurosky.android.NeuroData;
import com.neurosky.android.NeuroSky;
import com.neurosky.android.NeuroStreamParser;

public class MindSetListener extends Handler {

	public static final Boolean RAW_DATA_ENABLE = false;
	private static final boolean LOGGER_ENABLE = false;
	private static final String LOGGER_TAG = "NEURO_SKY";

	public static int PERMISSION_DENIED = 1;
	public static int POSITION_UNAVAILABLE = 2;
	public static int TIMEOUT = 3;
	public static final String CLIENT_METHODS = "window.plugins.mindset";
	String id;

	private final MindSetPlugin broker;
	int interval;

	BluetoothAdapter btAdapter;
	BluetoothDevice device;
	NeuroSky neuroSky;

	public MindSetListener(MindSetPlugin broker, String id, int timeout,
			String deviceAddress) {
		this.id = id;
		this.interval = timeout;
		this.broker = broker;

		this.btAdapter = BluetoothAdapter.getDefaultAdapter();
		this.device = btAdapter.getRemoteDevice(deviceAddress);
		this.neuroSky = new NeuroSky(btAdapter, this);
	}

	@Override
	public void handleMessage(android.os.Message msg) {
		switch (msg.what) {

		case (NeuroSky.MESSAGE_BT_DEVICE_NAME):
			if (LOGGER_ENABLE)
				Log.d(LOGGER_TAG, "[ MESSAGE_BT_DEVICE_NAME ]");
			break;

		case (NeuroSky.MESSAGE_BT_STATUS_CHANGE):
			if (LOGGER_ENABLE)
				Log.d(LOGGER_TAG, "[ MESSAGE_BT_STATUS_CHANGE ]");

			int changedStatus = msg.arg1;
			if (changedStatus == NeuroSky.BT_STATE_NONE) {
				Log.i(LOGGER_TAG, "BT_STATE_NONE");
			} else if (changedStatus == NeuroSky.BT_STATE_CONNECTING) {
				Log.i(LOGGER_TAG, "BT_STATE_CONNECTING");
			} else if (changedStatus == NeuroSky.BT_STATE_CONNECTED) {
				Log.i(LOGGER_TAG, "BT_STATE_CONNECTED");
			}
			break;

		case (NeuroSky.MESSAGE_BT_TOAST):
			if (LOGGER_ENABLE)
				Log.d(LOGGER_TAG, "[ MESSAGE_BT_TOAST ]");
			// String message = msg.getData().getString("toast");
			// Toast.makeText(getApplicationContext(),
			// msg.getData().getString("toast"), Toast.LENGTH_SHORT)
			// .show();
			break;

		case (NeuroStreamParser.MESSAGE_READ_DIGEST_DATA_PACKET):
			if (LOGGER_ENABLE)
				Log.d(LOGGER_TAG, "[ MESSAGE_READ_DIGEST_DATA_PACKET ]");
			NeuroData data = (NeuroData) msg.obj;
			this.success(data.getSignal(), data.getAttention(), data
					.getMeditation());
			Log.i(LOGGER_TAG, "All values : " + data.getAllString());
			break;

		case (NeuroStreamParser.MESSAGE_READ_RAW_DATA_PACKET):
			if (LOGGER_ENABLE)
				Log.d(LOGGER_TAG, "[ MESSAGE_READ_RAW_DATA_PACKET ]");
			// NeuroRawData rawData = (NeuroRawData) msg.obj;
			// Log.i(LOGGER_TAG, "Raw values : " +
			// rawData.getRawWaveValue());
			if (RAW_DATA_ENABLE) {
				// rawView.setText(rawData.getRawWaveValue());
			}
			break;
		}
	}

	public void destroy() {
		stop();
	}

	void success(int signalStrength, int attention, int meditation) {
		String params = "{'signal':" + signalStrength + ", 'attention':"
				+ attention + ", 'meditation':" + meditation + "}";

		this.broker.sendJavascript(CLIENT_METHODS + ".success('" + this.id
				+ "'," + params + ");");
	}

	void fail(int code, String msg) {
		this.broker.sendJavascript(CLIENT_METHODS + ".fail('" + this.id + "', "
				+ ", " + code + ", '" + msg + "');");
		stop();
	}

	void start(int interval) {

		if (this.neuroSky != null) {
			this.neuroSky.connect(device);
			this.neuroSky.start(RAW_DATA_ENABLE);
		}

		if (this.neuroSky == null)
			fail(POSITION_UNAVAILABLE, "No location providers available.");
	}

	void stop() {
		if (this.neuroSky != null) {
			this.neuroSky.stop();
			this.neuroSky.close();
		}
	}

}
