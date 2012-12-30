package com.wiley.wrox.parking_assistant;

import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wiley.wroxaccessories.UsbConnection12;
import com.wiley.wroxaccessories.WroxAccessory;

public class MainActivity extends Activity {

	/** The Wrox Accessory class, handles communication for us */
	private WroxAccessory mAccessory;

	/**
	 * The USB Manager, change this to com.android.future.usb.UsbManager if you
	 * want the SDK 10 version of the Accessory
	 */
	private UsbManager mUsbManager;

	/**
	 * The Connection object, need to change this too if you want to use another
	 * type of accessory.
	 */
	private UsbConnection12 connection;

	// UI
	private ProgressBar mProgressBar;
	private TextView mTextView;

	/** Used to identify the subscription in the broadcast reciever */
	private String subscription;

	/** Used to uniquely identify each message (only for use by the client) */
	private int id = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 1. Get a reference to the UsbManager (there's only one, so you never
		// instantiate it)
		mUsbManager = (UsbManager) getSystemService(USB_SERVICE);

		// 2. Create the Connection object
		connection = new UsbConnection12(this, mUsbManager);

		// 3. Instantiate the WroxAccessory
		mAccessory = new WroxAccessory(this);

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		mProgressBar.setMax(6);
		mProgressBar.setProgress(0);

		mTextView = (TextView) findViewById(R.id.textView1);
		mTextView.setText(0 + " m");
	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			// 4. Initiate a connection to the accessory, using the connection
			// object
			mAccessory.connect(WroxAccessory.USB_ACCESSORY_12, connection);

			// 5. subscribe to the topic - "us" in this case stands for Ultra
			// Sound
			subscription = mAccessory.subscribe(receiver, "us", id++);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// 6. Disconnect the accessory
		try {
			mAccessory.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 7. Create the reciever and act on the data
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(subscription)) {
				byte[] payload = intent.getByteArrayExtra(subscription + ".payload");

				mProgressBar.setProgress(payload[0]);
				mTextView.setText(payload[0] + " m");
			}
		}
	};
}
