package com.erjr.diabetesi1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.erjr.cloop.dao.SGVDataSource;
import com.erjr.cloop.dao.CoursesDataSource;
import com.erjr.cloop.entities.SGV;
import com.erjr.cloop.entities.Course;

public class BTSyncServer extends Thread {
	private final BluetoothServerSocket mmServerSocket;
	private BluetoothAdapter mBluetoothAdapter;
	private String NAME = "DiabetesBTSync";
	private static final String TAG = "BTSyncServer";
	public static final UUID MY_UUID = UUID
			.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");

	private SGVDataSource SGVDS = null;
	private CoursesDataSource CoursesDS = null;

	public BTSyncServer(Context context) {
		SGVDS = new SGVDataSource(context);
		CoursesDS = new CoursesDataSource(context);
		// Use a temporary object that is later assigned to mmServerSocket,
		// because mmServerSocket is final
		BluetoothServerSocket tmp = null;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		try {
			// MY_UUID is the app's UUID string, also used by the client code
			// tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME ,
			// BTSync.MY_UUID);
			// TODO: possibly change to secure connection
			tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
					NAME, MY_UUID);
		} catch (IOException e) {
		}
		mmServerSocket = tmp;
	}

	public void run() {
		BluetoothSocket socket = null;
		String dataReceived = null;
		// Keep listening until exception occurs or a socket is returned
		Log.i(TAG,
				"Going to wait for a connection on :"
						+ mmServerSocket.toString());
		while (true) {
			try {
				socket = mmServerSocket.accept();
			} catch (IOException e) {
				break;
			}
			// If a connection was accepted
			if (socket != null) {
				Log.i(TAG, "connection made!!");
				// Do work to manage the connection (in a separate thread)
				// send data and read data back
				dataReceived = sync(socket, getDataToSend());
				try {
					mmServerSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		if (dataReceived != null) {
			processDataReceived(dataReceived);
		}
	}

	/**
	 * process any data that is received from the device
	 * 
	 * @param dataReceived
	 */
	private void processDataReceived(String dataReceived) {
		String fullSGVXml = Util.getValueFromXml(dataReceived, SGV.TABLE_SGVS);
		if(fullSGVXml == null || fullSGVXml.isEmpty()) {
			return;
		}
		String[] sgvsXmlAsArray = Util.getValuesFromXml(fullSGVXml,
				SGV.ROW_DESC);

		for (String sgvXml : sgvsXmlAsArray) {
			SGV sgv = new SGV();
			sgv.setFromXML(sgvXml);
			SGVDS.saveSGV(sgv);
		}
	}

	private String getDataToSend() {
		String xml = getCourseDataToSend();
		return xml;
	}

	private String getCourseDataToSend() {
		List<Course> courses = CoursesDS.getCoursesToTransfer();
		String transXml = "<courses>";
		if (courses != null) {
			for (Course course : courses) {
				transXml += course.toXML();
			}
		}
		transXml += "</courses>";
		return transXml;
	}

	/**
	 * sends data (dataToSend) then receives and returns the data.
	 * 
	 * @param btSocket
	 * @param dataToSend
	 * @return
	 */
	private String sync(BluetoothSocket btSocket, String dataToSend) {
		OutputStream outStream = null;
		try {
			outStream = btSocket.getOutputStream();
		} catch (IOException e) {
			Log.i(TAG,
					"In onResume() and output stream creation failed:"
							+ e.getMessage() + ".");
		}

		byte[] msgBuffer = dataToSend.getBytes();
		try {
			Log.i(TAG, "writing : " + dataToSend);
			outStream.write(msgBuffer);
			outStream.write("</EOM>".getBytes());
		} catch (IOException e) {
			String msg = "In sync() and an exception occurred during write: "
					+ e.getMessage();
			msg = msg + ".\n\nCheck that the SPP UUID: " + MY_UUID.toString()
					+ " exists on server.\n\n";

			Log.i(TAG, msg);
		}
		Log.i(TAG, "done writing, going to read...");
		int treshHold = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			while (btSocket.getInputStream().available() == 0
					&& treshHold < 3000) {
				Thread.sleep(1);
				treshHold++;
			}
			baos.reset();
			while (btSocket.getInputStream().available() > 0) {
				baos.write(btSocket.getInputStream().read());
				Thread.sleep(1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String dataReceived = new String(baos.toByteArray());
		Log.i(TAG, "done reading : " + dataReceived);
		return dataReceived;
	}

	/** Will cancel the listening socket, and cause the thread to finish */
	public void cancel() {
		try {
			mmServerSocket.close();
		} catch (IOException e) {
		}
	}
}