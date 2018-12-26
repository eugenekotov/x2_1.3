package com.kotov.x2.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kotov.x2.R;
import com.kotov.x2.formula.LetterFormula;
import com.kotov.x2.formula.ListFormula;
import com.kotov.x2.formula.PowerFormula;
import com.kotov.x2.widgets.FormulaView;

public class HelpActivity extends Activity {

	private ScrollView scrollViewHelp;
	private TextView textViewHelp1;
	private TextView textViewHelp2;
	private TextView textViewHelp3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		// ad
//		AdView mAdView = (AdView) findViewById(R.id.adViewHelp);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
        //
		init();
	}

	private void init() {
		scrollViewHelp = findViewById(R.id.scrollViewHelp);
		textViewHelp1 = findViewById(R.id.textViewHelp1);
		textViewHelp2 = findViewById(R.id.textViewHelp2);
		textViewHelp3 = findViewById(R.id.textViewHelp3);
		
		FormulaView formulaEquation = findViewById(R.id.formulaEquation);
		ListFormula listConstruction = new ListFormula();
		listConstruction.add(new PowerFormula(new LetterFormula("ax"), new LetterFormula("2")));
		listConstruction.add(new LetterFormula("+bx+c=0"));
		formulaEquation.setFormula(listConstruction);
		formulaEquation.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size));
	}

	public void onClickHelpContent1(View view) {
		scrollTo(textViewHelp1);
	}

	public void onClickHelpContent2(View view) {
		scrollTo(textViewHelp2);
	}
	
	public void onClickHelpContent3(View view) {
		scrollTo(textViewHelp3);
	}
	
	private void scrollTo(final View view) {
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				scrollViewHelp.scrollTo(0, view.getTop());
			}
		});
	}
	
}
