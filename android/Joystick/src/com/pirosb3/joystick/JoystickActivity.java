package com.pirosb3.joystick;

import java.io.PrintWriter;
import java.net.Socket;

import com.pirosb3.joystick.components.JoystickMovedListener;
import com.pirosb3.joystick.components.JoystickView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class JoystickActivity extends Activity {
	protected Socket _currentSocket;
	protected PrintWriter _socketOutputStreamWriter;
	protected TextView _statusTextView;
	protected Button _connectButton;
	protected EditText _connectTextfield;
	protected JoystickView _joystick;
	protected boolean _connected;
	
	public EditText getConnectTextField() { return _connectTextfield; }
	public PrintWriter getSocketOutputStreamWriter() {
		if (!_connected) return null;
		return _socketOutputStreamWriter; 
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        _statusTextView = (TextView)findViewById(R.id.status);
        _connectButton = (Button)findViewById(R.id.connectButton);
        _connectTextfield = (EditText)findViewById(R.id.connectTextField);
        _joystick = (JoystickView)findViewById(R.id.joystick);
        
        _connectButton.setOnClickListener(new ConnectClickListener(this));
        _joystick.setOnJostickMovedListener(new MovementTouchListener(this));
    }
    
    @Override
    protected void onPause() {
    	_connected = false;
    	_socketOutputStreamWriter = null;
    	_currentSocket = null;
    	_statusTextView.setText("Ready");
    	super.onPause();
    }
    
    protected void initializeSocket(String path, int port) {
    	_connected = false;
    	_statusTextView.setText("Connecting");
    	
    	// Try to connect
    	try {
			_currentSocket = new Socket(path, port);
			_socketOutputStreamWriter = new PrintWriter(_currentSocket.getOutputStream(), true);
			_statusTextView.setText("Connected");
			_connected = true;
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
			_statusTextView.setText("Failed");
			e.printStackTrace();
		}
    }
}

class ConnectClickListener implements OnClickListener {
	private JoystickActivity _activity;
	
	public ConnectClickListener(JoystickActivity activity) {
		_activity = activity;
	}
	
	@Override
	public void onClick(View v) {
		// Get IP Address, return if none
		String address = _activity.getConnectTextField().getText().toString();
		if (address.length() == 0) return;
		
		_activity.initializeSocket(address, 8000);
	}
}

class MovementTouchListener implements JoystickMovedListener {
	
	private JoystickActivity _activity;
	
	public MovementTouchListener(JoystickActivity activity) {
		_activity = activity;
	}
	
	@Override
	public void OnMoved(int pan, int tilt) {
		
		// If not connected yet, just return
		if (!_activity._connected) return;
		
		_activity.getSocketOutputStreamWriter().println(pan + "," + tilt);
	}

	@Override
	public void OnReleased() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnReturnedToCenter() {
		// TODO Auto-generated method stub
		
	}
	
}