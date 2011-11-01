package com.pirosb3.joystick;

import java.io.PrintWriter;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class JoystickTouchListener implements OnTouchListener {
	protected byte joystickEvent;
	protected PrintWriter writer;
	
	public JoystickTouchListener(PrintWriter writer, byte event) {
		this.writer = writer;
		joystickEvent = event;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			writer.println(joystickEvent);
			break;
		case MotionEvent.ACTION_UP:
			writer.println(JoystickEvent.END);
			break;
		}
		return false;
	}
}