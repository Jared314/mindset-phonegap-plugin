package com.bluecasedevelopment.mindset;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;
import com.phonegap.api.PluginResult.Status;

public class BluetoothPlugin extends Plugin {

	public static final String LIST_ACTION = "list";

	public BluetoothPlugin() {
	}

	@Override
	public boolean isSynch(String action) {
		return true;
	}

	@Override
	public PluginResult execute(String action, JSONArray args, String callbackId) {
		Log.d("BluetoothPlugin", "Plugin Called");
		PluginResult result = null;
		PluginResult.Status status = PluginResult.Status.OK;

		if (LIST_ACTION.equals(action)) {
			Log.d("BluetoothPlugin", "Start passed");
			List<String> addressList = list();
			JSONArray j = new JSONArray(addressList);
			result = new PluginResult(status, j);

		} else {
			result = new PluginResult(Status.INVALID_ACTION);
			Log.d("BluetoothPlugin", "Invalid action : " + action + " passed");
		}
		return result;
	}

	@Override
	public void onDestroy() {
	}

	private List<String> list() {
		List<String> result = new ArrayList<String>();

		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				// pairedDeviceArrayAdapter.add(device.getName() + "\n" +
				// device.getAddress());
				result.add(device.getAddress());
			}
		}

		// String[] resultArray = new String[result.size()];
		// resultArray = result.toArray(resultArray);

		return result;
	}
}
