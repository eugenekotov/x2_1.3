package com.kotov.x2.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.kotov.x2.R;

public class AskRateActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ask_rate);
		init();
	}

	private void init() {
		((Button) findViewById(R.id.buttonRateIt)).setOnClickListener(this);
		((Button) findViewById(R.id.buttonRemindLater)).setOnClickListener(this);
		((Button) findViewById(R.id.buttonDontRate)).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		Button button = (Button) view;
		Intent intent = new Intent();
		intent.putExtra(MainActivity.KEY_ASK_RATE_RESULT, button.getText());
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

}
