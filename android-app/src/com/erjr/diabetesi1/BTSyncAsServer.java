package com.erjr.diabetesi1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

public class BTSyncAsServer extends Activity {

	// TextView out;
	private static final String TAG = "BTSYNCAsServer";
	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;

	// Well known SPP UUID
	private static final UUID MY_UUID = UUID
			.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");

	// Insert your server's MAC address
	private static String address = "00:02:72:C9:63:00"; // dongle mac
	// private static String address = "00:22:43:F3:A6:4B"; // ubuntu mac

	public BTSyncAsServer() {
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		CheckBTState();

		// Set up a pointer to the remote node using it's address.
		BluetoothDevice device = btAdapter.getRemoteDevice(address);

		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice d : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// ListView
				Log.i(TAG, "\n" + d.getName() + " - " + d.getAddress()
						+ " - is bonded: " + d.getBondState());
			}
		}
		
		device.createBond();

		// Two things are needed to make a connection:
		// A MAC address, which we got above.
		// A Service ID or UUID. In this case we are using the
		// UUID for SPP.
		try {
			btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
			AlertBox("Fatal Error", "In onResume() and socket create failed: "
					+ e.getMessage() + ".");
		}
		
		Log.i(TAG, " is connected : "+btSocket.isConnected());
		// Discovery is resource intensive. Make sure it isn't going on
		// when you attempt to connect and pass your message.
		btAdapter.cancelDiscovery();

		// Establish the connection. This will block until it connects.
		try {
			Log.i(TAG, "\n...About to connect to socket...");
			btSocket.connect();
			Log.i(TAG, "\n...Connection established and data link opened...");
		} catch (IOException e) {
			Log.i(TAG, "\n...Failed to connect to socket..." + e.getMessage());
			try {
				btSocket.close();
			} catch (IOException e2) {
				AlertBox("Fatal Error",
						"In onResume() and unable to close socket during connection failure"
								+ e2.getMessage() + ".");
			}
		}
	}

	public void write(String message) {
		try {
			outStream = btSocket.getOutputStream();
		} catch (IOException e) {
			AlertBox(
					"Fatal Error",
					"In onResume() and output stream creation failed:"
							+ e.getMessage() + ".");
		}

		byte[] msgBuffer = message.getBytes();
		try {
			outStream.write(msgBuffer);
			outStream.write("</EOM>".getBytes());
		} catch (IOException e) {
			String msg = "In onResume() and an exception occurred during write: "
					+ e.getMessage();
			if (address.equals("00:00:00:00:00:00"))
				msg = msg
						+ ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code";
			msg = msg + ".\n\nCheck that the SPP UUID: " + MY_UUID.toString()
					+ " exists on server.\n\n";

			AlertBox("Fatal Error", msg);
		}
	}

	// Wait for incomming data
	// if is something in the buffer start count but max 3sec
	// 3 sec is defined by my device vendor for reply
	//
	// after it we something on buffer.
	// you must realize the reading is on the fly
	// there for you must wait after read to next data sleep(1mxec)
	// until data are available
	// then return
	public String read() {
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
		return new String(baos.toByteArray());
	}

	public void onPause() {
		super.onPause();

		// out.append("\n...In onPause()...");

		if (outStream != null) {
			try {
				outStream.flush();
			} catch (IOException e) {
				AlertBox(
						"Fatal Error",
						"In onPause() and failed to flush output stream: "
								+ e.getMessage() + ".");
			}
		}

		try {
			btSocket.close();
		} catch (IOException e2) {
			AlertBox("Fatal Error", "In onPause() and failed to close socket."
					+ e2.getMessage() + ".");
		}
	}

	private void CheckBTState() {
		// Check for Bluetooth support and then check to make sure it is turned
		// on

		// Emulator doesn't support Bluetooth and will return null
		if (btAdapter == null) {
			AlertBox("Fatal Error", "Bluetooth Not supported. Aborting.");
		} else {
			if (btAdapter.isEnabled()) {
				Log.i(TAG, "\n...Bluetooth is enabled...");
			} else {
				// Prompt user to turn on Bluetooth
				Log.e(TAG, "Bluetooth is disabled, about to ask to start.");
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}

	public void AlertBox(String title, String message) {
		Log.e(TAG, title + " - " + message);
		// new AlertDialog.Builder(this).setTitle(title)
		// .setMessage(message + " Press OK to exit.")
		// .setPositiveButton("OK", new OnClickListener() {
		// public void onClick(DialogInterface arg0, int arg1) {
		// finish();
		// }
		// }).show();
	}

}
//
//
//class AcceptThread extends Thread {
//    private final BluetoothServerSocket mmServerSocket;
//	private BluetoothAdapter btAdapter;
//	private static final UUID MY_UUID = UUID
//			.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
//	private static final String NAME = "BTSyncAsServer";
// 
//    public AcceptThread() {
//    	btAdapter = BluetoothAdapter.getDefaultAdapter();
//        // Use a temporary object that is later assigned to mmServerSocket,
//        // because mmServerSocket is final
//        BluetoothServerSocket tmp = null;
//        try {
//            // MY_UUID is the app's UUID string, also used by the client code
//            tmp = btAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
//        } catch (IOException e) { }
//        mmServerSocket = tmp;
//    }
// 
//    public void run() {
//        BluetoothSocket socket = null;
//        // Keep listening until exception occurs or a socket is returned
//        while (true) {
//            try {
//                socket = mmServerSocket.accept();
//            } catch (IOException e) {
//                break;
//            }
//            // If a connection was accepted
//            if (socket != null) {
//                // Do work to manage the connection (in a separate thread)
//                manageConnectedSocket(socket);
//                try {
//					mmServerSocket.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//                break;
//            }
//        }
//    }
// 
//    private void manageConnectedSocket(BluetoothSocket socket) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	/** Will cancel the listening socket, and cause the thread to finish */
//    public void cancel() {
//        try {
//            mmServerSocket.close();
//        } catch (IOException e) { }
//    }
//}
//
//class ConnectedThread extends Thread {
//    private final BluetoothSocket mmSocket;
//    private final InputStream mmInStream;
//    private final OutputStream mmOutStream;
// 
//    public ConnectedThread(BluetoothSocket socket) {
//        mmSocket = socket;
//        InputStream tmpIn = null;
//        OutputStream tmpOut = null;
// 
//        // Get the input and output streams, using temp objects because
//        // member streams are final
//        try {
//            tmpIn = socket.getInputStream();
//            tmpOut = socket.getOutputStream();
//        } catch (IOException e) { }
// 
//        mmInStream = tmpIn;
//        mmOutStream = tmpOut;
//    }
// 
//    public void run() {
//        byte[] buffer = new byte[1024];  // buffer store for the stream
//        int bytes; // bytes returned from read()
// 
//        // Keep listening to the InputStream until an exception occurs
//        while (true) {
//            try {
//                // Read from the InputStream
//                bytes = mmInStream.read(buffer);
//                // Send the obtained bytes to the UI activity
//                mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
//                        .sendToTarget();
//            } catch (IOException e) {
//                break;
//            }
//        }
//    }
// 
//    /* Call this from the main activity to send data to the remote device */
//    public void write(byte[] bytes) {
//        try {
//            mmOutStream.write(bytes);
//        } catch (IOException e) { }
//    }
// 
//    /* Call this from the main activity to shutdown the connection */
//    public void cancel() {
//        try {
//            mmSocket.close();
//        } catch (IOException e) { }
//    }
//}