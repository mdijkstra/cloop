package com.erjr.diabetesi1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;


public class AcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;
	private BluetoothAdapter mBluetoothAdapter;
	private String NAME = "BTSync";
	private static final String TAG = "AcceptThread";
	public static final UUID MY_UUID = UUID
			.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
 
    public AcceptThread() {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        try {
            // MY_UUID is the app's UUID string, also used by the client code
//            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME , BTSync.MY_UUID);
            tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) { }
        mmServerSocket = tmp;
    }
 
    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        Log.i(TAG, "Going to wait for a connection on :"+mmServerSocket.toString());
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
                sync(socket);
                try {
					mmServerSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                break;
            }
        }
    }
 
    private void sync(BluetoothSocket btSocket) {
    	Log.i(TAG, "in sync()");
    	String message = "<courses></courses>";
    	OutputStream outStream = null;
		try {
			outStream = btSocket.getOutputStream();
		} catch (IOException e) {
			Log.i(TAG,
					"In onResume() and output stream creation failed:"
							+ e.getMessage() + ".");
		}

		byte[] msgBuffer = message.getBytes();
		try {
			Log.i(TAG, "writing...");
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
		Log.i(TAG, "done reading...");
		String latestSgXml = new String(baos.toByteArray());
		Log.i(TAG, latestSgXml);
	}

	/** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
}