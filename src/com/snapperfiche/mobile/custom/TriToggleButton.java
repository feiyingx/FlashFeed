package com.snapperfiche.mobile.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.snapperfiche.mobile.R;



public class TriToggleButton extends Button {

	private int _state = 0;
	
	//Get the attributes created in states.xml
	private static final int[] STATE_ONE_SET =
	{
		R.attr.state_one
	};
	
	private static final int[] STATE_TWO_SET =
	{
		R.attr.state_two
	};

	private static final int[] STATE_THREE_SET =
	{
		R.attr.state_three
	};
	
	public TriToggleButton(Context context)
	{
		super(context);
		
		_state = 0;
		this.setText("1");
	}
	
	public TriToggleButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		_state = 0;
		this.setText("1");
	}
	
	public TriToggleButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		
		_state = 0;
		this.setText("1");
	}
	
	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		// TODO Auto-generated method stub
		
		// Add the number of states
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 3);
		
		if (_state == 0) {
			mergeDrawableStates(drawableState, STATE_ONE_SET);
		} else if (_state == 1) {
			mergeDrawableStates(drawableState, STATE_TWO_SET);
		} else if (_state == 2) {
			mergeDrawableStates(drawableState, STATE_THREE_SET);
		}
		
		return drawableState;
	}
	
	@Override
	public boolean performClick() {
		// TODO Auto-generated method stub
		nextState();
		
		return super.performClick();
	}
	
	public void setState(int state)
	{
		if((state > -1) && (state < 3))
		{
			_state = state;
			setButtonText();
		}
	}
	
	public int getState()
	{
		return _state;
	}
	
	public void nextState() {
		_state++;
		
		if (_state > 2) {
			_state = 0;
		}
		
		setButtonText();
	}
	
	private void setButtonText()
	{
		switch(_state)
		{
			case 0: this.setText("1");
					break;
			case 1: this.setText("2");
					break;
			case 2: this.setText("3");
					break;
			default: this.setText("N/A");
					break;
		}
	}
	
	
}
