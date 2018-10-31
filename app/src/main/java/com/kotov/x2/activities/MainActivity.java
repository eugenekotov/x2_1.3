package com.kotov.x2.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.kotov.x2.R;
import com.kotov.x2.calculator.Calculator;
import com.kotov.x2.drawer.DrawUtils;
import com.kotov.x2.formula.LetterFormula;
import com.kotov.x2.formula.ListFormula;
import com.kotov.x2.formula.PowerFormula;
import com.kotov.x2.formula.TextPart;
import com.kotov.x2.solution.Solution;
import com.kotov.x2.widgets.FormulaView;
import com.kotov.x2.widgets.FormulaView.FormulaViewAlignment;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    public static final String KEY_ASK_RATE_RESULT = "askRateResult";
    private static final String APP_MARKET_URL = "market://details?id=";
    private static final String APP_WEB_URL = "http://play.google.com/store/apps/details?id=";
    private static final int REQUEST_CODE_ASK_RATE = 1;
    private static final String KEY_IS_SOLUTION_SHOWING = "isSolutionShowing";
    private static final String KEY_NEED_ASK_RATE = "needAskRate";
    private static final String KEY_LAUNCH_COUNT = "launchCount";
    private static final String KEY_NEED_SHOW_NEWS = "needShowNews";
    private static final String KEY_SHOW_NEWS_COUNT = "showNewsCount";
    private static final int SHOW_NEWS_MAX_COUNT = 3;
    private EditText editTextA;
    private EditText editTextB;
    private EditText editTextC;
    private ImageButton buttonExecute;
    private ScrollView scrollView;
    private ImageView drawingImageView;
    private Timer remindTimer;
    private TimerTask remindTimerTask;
    private ViewGroup layoutMain;
    private LinearLayout layoutWindowTitle;
    private LinearLayout layoutCondition;
    private FormulaView formulaX2;
    private FormulaView formulaX;
    private FormulaView formula0;
    private int cellColor;
    private Solution solution;
    private boolean isSolutionShowing = false;
    private boolean enableEditListener = true;
    private boolean isAnswerAreaHeightCaclculated = false;
    private int answerAreaHeight = 0;
    private int textSize;
    private int orientation;
    // Ask Rate
    private boolean needAskRate = true;
    private int launchCount = 0;
    // News
    private boolean needShowNews = false;
    private int showNewsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ad
//		AdView mAdView = (AdView) findViewById(R.id.adViewMain);
//		AdRequest adRequest = new AdRequest.Builder().build();
//		mAdView.loadAd(adRequest);
        //
        init();
    }

    private void init() {
        remindTimer = new Timer();
        // variants
        // Configuration.ORIENTATION_PORTRAIT
        // Configuration.ORIENTATION_LANDSCAPE
        orientation = getResources().getConfiguration().orientation;
        //
        initDrawer();
        initTextSize();
        initConditionArea();
        initAnswerArea();
    }

    private void initTextSize() {
        textSize = getResources().getDimensionPixelSize(R.dimen.text_size);
    }

    private void initDrawer() {
//		DrawUtils.setTypeface(Typeface.createFromAsset(getAssets(), "font/timesnewroman.ttf"));
        // TODO fix font for drawer
        DrawUtils.initScreeSize(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!isAnswerAreaHeightCaclculated) {
            calcAnswerAreaHeight();
            if (isSolutionShowing) {
                calc();
                printResult();
            } else {
                drawCells();
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    showKeyboard();
                }
            }
        }
    }

    private void initAnswerArea() {
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        drawingImageView = (ImageView) this.findViewById(R.id.drawingImageView);
        cellColor = ContextCompat.getColor(getApplicationContext(), R.color.cell_color);
    }

    private void initEdits() {

        editTextA = initEditText((EditText) findViewById(R.id.editTextA));
        editTextA.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                clearAnswerArea();
                if (!enableEditListener) {
                    return;
                }
                stopReminder();
                if (editTextB.getText().toString().trim().isEmpty()) {
                    remindToView(editTextB);
                } else if (editTextC.getText().toString().trim().isEmpty()) {
                    remindToView(editTextC);
                } else {
                    remindToView(buttonExecute);
                }
            }
        });

        editTextB = initEditText((EditText) findViewById(R.id.editTextB));
        editTextB.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                clearAnswerArea();
                if (!enableEditListener) {
                    return;
                }
                stopReminder();
                if (editTextB.getText().toString().trim().isEmpty()) {
                    remindToView(editTextB);
                } else if (editTextC.getText().toString().trim().isEmpty()) {
                    remindToView(editTextC);
                } else {
                    remindToView(buttonExecute);
                }
            }
        });

        editTextC = initEditText((EditText) findViewById(R.id.editTextC));
        editTextC.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                clearAnswerArea();
                if (!enableEditListener) {
                    return;
                }
                stopReminder();
                if (editTextC.getText().toString().trim().isEmpty()) {
                    remindToView(editTextC);
                } else {
                    remindToView(buttonExecute);
                }
            }
        });
        editTextC.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    execute();
                }
                return false;
            }
        });
    }

    private EditText initEditText(EditText editText) {
        editText.setTypeface(DrawUtils.getTypeface());
        return editText;
    }

    private void clearAnswerArea() {
        if (!isSolutionShowing) {
            return;
        }
        isSolutionShowing = false;
        drawCells();
    }

    private void drawCells() {
        if (!isAnswerAreaHeightCaclculated) {
            return;
        }
        Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(), answerAreaHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        DrawUtils.drawCell(canvas, textSize, cellColor);
        if (needShowNews) {
            Solution news = new Solution();
            news.add(new TextPart(getString(R.string.news_text_1_1)));
            news.add(new TextPart(getString(R.string.news_text_1_2)));
            news.print(canvas, textSize);
        } else {
			/*
			----------Part for debag---------------------------------  
			Solution news = new Solution();
			news.add(new TextPart("launchCount == " + launchCount));
			news.add(new TextPart("needAskRate == " + needAskRate));
			// News
			news.add(new TextPart("needShowNews == " + needShowNews));
			news.add(new TextPart("showNewsCount == " + showNewsCount));
			news.print(canvas, textSize);
			*/
        }
        drawingImageView.setImageBitmap(bitmap);
    }

    private void calcAnswerAreaHeight() {
        answerAreaHeight = layoutMain.getHeight() - layoutWindowTitle.getHeight() - layoutCondition.getHeight();
        isAnswerAreaHeightCaclculated = true;
    }

    private void initConditionArea() {

        layoutMain = (ViewGroup) findViewById(R.id.layoutMain);
        layoutWindowTitle = (LinearLayout) findViewById(R.id.layoutAppTitle);
        layoutCondition = (LinearLayout) findViewById(R.id.layoutCondition);
        // x2
        formulaX2 = (FormulaView) findViewById(R.id.formulaX2);
        ListFormula listConstruction = new ListFormula();
        listConstruction.add(new PowerFormula(new LetterFormula("x"), new LetterFormula("2")));
        listConstruction.add(new LetterFormula("+"));
        formulaX2.setFormula(listConstruction);
        formulaX2.setTextSize(textSize);
        Rect bounds = formulaX2.getFormula().getBounds(textSize);
        formulaX2.setMinimumHeight(bounds.height());
        // x
        formulaX = (FormulaView) findViewById(R.id.formulaX);
        formulaX.setFormulaViewAlignment(FormulaViewAlignment.BOTTOM);
        formulaX.setFormula(new LetterFormula("x+"));
        formulaX.setTextSize(textSize);
        formulaX.setMinimumHeight(bounds.height());
        // 0
        formula0 = (FormulaView) findViewById(R.id.formula0);
        formula0.setFormulaViewAlignment(FormulaViewAlignment.BOTTOM);
        formula0.setFormula(new LetterFormula("=0"));
        formula0.setTextSize(textSize);
        formula0.setMinimumHeight(bounds.height());
        //
        initEdits();
        //
        buttonExecute = (ImageButton) findViewById(R.id.buttonExecute);
    }

    public void onClickButtonExecute(View view) {
        hideKeyboard();
        execute();
    }

    public void onClickButtonClear(View view) {
        stopReminder();
        enableEditListener = false;
        editTextA.setText("");
        editTextA.requestFocus();
        editTextB.setText("");
        editTextC.setText("");
        enableEditListener = true;
        clearAnswerArea();
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            showKeyboard();
        }
        remindToView(editTextA);
    }

    public void onClickButtonHelp(View view) {
        stopReminder();
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    private void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    private void hideKeyboard() {
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void execute() {
        stopReminder();
        if (isSolutionShowing) {
            return;
        }
        isSolutionShowing = true;
        calc();
        printResult();
    }

    private void printResult() {
        scrollView.scrollTo(0, 0);
        int width = scrollView.getWidth();
        if (width == 0) {
            return;
        }
        Canvas canvas = new Canvas();
        int height = Math.max(solution.getHeight(textSize), answerAreaHeight);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        DrawUtils.drawCell(canvas, textSize, cellColor);
        solution.print(canvas, textSize);
        drawingImageView.setImageBitmap(bitmap);
    }

    private void calc() {
        solution = new Calculator(getApplicationContext(), getFloatValue(editTextA), getFloatValue(editTextB), getFloatValue(editTextC)).getSolution();
    }

    private double getFloatValue(EditText editText) {
        String stringValue = editText.getText().toString().trim();
        if (stringValue.length() == 0) {
            return 0;
        }
        try {
            return Double.parseDouble(stringValue);
        } catch (NumberFormatException e) {
        }
        if (stringValue.contains("-")) {
            return -1;
        }
        return 1;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        enableEditListener = false;
        super.onRestoreInstanceState(savedInstanceState);
        enableEditListener = true;
        isSolutionShowing = savedInstanceState.getBoolean(KEY_IS_SOLUTION_SHOWING, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_SOLUTION_SHOWING, isSolutionShowing);
    }

    @Override
    protected void onResume() {
        loadPrefs();
        if (!isSolutionShowing) {
            if (editTextA.getText().toString().trim().isEmpty()) {
                remindToView(editTextA);
            } else if (editTextB.getText().toString().trim().isEmpty()) {
                remindToView(editTextB);
            } else if (editTextC.getText().toString().trim().isEmpty()) {
                remindToView(editTextC);
            } else {
                remindToView(buttonExecute);
            }
        }
        super.onResume();
    }

    private void loadPrefs() {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        needAskRate = sPref.getBoolean(KEY_NEED_ASK_RATE, true);
        launchCount = sPref.getInt(KEY_LAUNCH_COUNT, 0) + 1;
        //
        needShowNews = sPref.getBoolean(KEY_NEED_SHOW_NEWS, true);
        if (needShowNews) {
            showNewsCount = sPref.getInt(KEY_SHOW_NEWS_COUNT, 0) + 1;
            if (launchCount == 1 || showNewsCount >= SHOW_NEWS_MAX_COUNT) {
                needShowNews = false;
            }
        }
    }

    @Override
    protected void onPause() {
        savePrefs();
        super.onPause();
    }

    private void savePrefs() {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(KEY_NEED_ASK_RATE, needAskRate);
        editor.putInt(KEY_LAUNCH_COUNT, launchCount);
        //
        editor.putBoolean(KEY_NEED_SHOW_NEWS, needShowNews);
        if (needShowNews) {
            editor.putInt(KEY_SHOW_NEWS_COUNT, showNewsCount);
        }
        //
        editor.commit();
    }

    private void remindToView(final View view) {
        final Handler uiHandler = new Handler();
        stopReminder();
        remindTimerTask = new TimerTask() {
            @Override
            public void run() {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.remind);
                        view.startAnimation(anim);
                    }
                });
            }
        };
        remindTimer.schedule(remindTimerTask, 4000);
    }

    public void onClickButtonShare(View view) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_text) + "\n" + APP_WEB_URL + getPackageName());
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_ASK_RATE) {
            String askRateResult = data.getStringExtra(KEY_ASK_RATE_RESULT);
            if (askRateResult.equals(getString(R.string.button_rate_it_title))) {
                openMarket();
                needAskRate = false;
            } else if (askRateResult.equals(getString(R.string.button_remind_later_title))) {
                needAskRate = true;
            } else {
                needAskRate = false;
            }
            savePrefs();
            finish();
        }
    }

    private void openMarket() {
        String packageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(APP_MARKET_URL + packageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(APP_WEB_URL + packageName)));
        }
    }

    private void askRate() {
        Intent intent = new Intent(this, AskRateActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ASK_RATE);
    }

    private boolean shouldAskRate() {
        if (launchCount % 5000 == 0) {
            needAskRate = true;
        }
        return needAskRate && (launchCount == 7 || launchCount % 14 == 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && shouldAskRate()) {
            askRate();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void stopReminder() {
        if (remindTimerTask != null) {
            remindTimerTask.cancel();
        }
    }

}
