package com.github.glomadrian.codeinputlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.github.glomadrian.codeinputlib.model.onEdittextChanged;
import com.github.glomadrian.codeinputlib.model.onTextMaxSize;

import java.lang.reflect.Method;

/**
 * Created by gerpsychosocial on 12/3/15.
 */
public class CodeInputViewGroup extends RelativeLayout implements onTextMaxSize {
    private LayoutInflater inflater;
    private CodeInput codeInput;
    private EditText fakeEdittext;
    private float textSize;
    private onCodeInputCompleted codeInputCompletedListener = null;
    private onKeyEvent onKeyEventListener = null;
    private onEdittextChanged listener = null;
    private float underlineWidth;
    private Boolean line;

    public boolean isAutoComplete;
    private String inputType;

    private int previousColor;
    private int underlineAmount;
    private int underlineColor;
    private int underlineSelectedColor;

    private int textColor;
    private float underlineStrokeWidth;
    private float textMarginBottom;
    private float circleRadius;

    public CodeInputViewGroup(Context context) {
        super(context);
        initView(context, null);
        initDefault();
        initCustom(context, null);
        init(context, null);
    }

    public CodeInputViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
        initDefault();
        initCustom(context, attrs);
        init(context, attrs);
    }


    public CodeInputViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        initDefault();
        initCustom(context, attrs);
        init(context, attrs);


    }

    private void initDefault() {
        textSize = getContext().getResources().getDimension(R.dimen.text_size);
        underlineWidth = getContext().getResources().getDimension(R.dimen.underline_width);
        underlineStrokeWidth = getContext().getResources().getDimension(R.dimen.underline_stroke_width);
        underlineColor = getContext().getResources().getColor(com.github.glomadrian.codeinputlib.R.color.underline_default_color);
        underlineSelectedColor = getContext().getResources().getColor(com.github.glomadrian.codeinputlib.R.color.underline_selected_color);
        underlineSelectedColor = getContext().getResources().getColor(com.github.glomadrian.codeinputlib.R.color.underline_selected_color);
        textColor = getContext().getResources().getColor(com.github.glomadrian.codeinputlib.R.color.textColor);
        textMarginBottom = getContext().getResources().getDimension(R.dimen.text_margin_bottom);
        previousColor = getContext().getResources().getColor(com.github.glomadrian.codeinputlib.R.color.underline_default_color);
        isAutoComplete = false;
        underlineAmount = 6;
        line = false;
        inputType = "character";
    }

//    public CodeInputViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        initView(context, attrs);
//
//        initCustom(context, attrs);
//        init(context, attrs);
//    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.code_input_view_group, this);
        codeInput = (CodeInput) this.findViewById(R.id.codeInput);
        fakeEdittext = (EditText) this.findViewById(R.id.fakeEditText);
        fakeEdittext.setTextColor(getResources().getColor(android.R.color.transparent));
        fakeEdittext.setCursorVisible(false);
        fakeEdittext.setBackgroundResource(android.R.color.transparent);
        fakeEdittext.clearFocus();
        fakeEdittext.requestFocus();

    }


    private void initCustom(Context context, AttributeSet attrs) {

        TypedArray attributes =
                getContext().obtainStyledAttributes(attrs, R.styleable.core_area);
        isAutoComplete = attributes.getBoolean(R.styleable.core_area_auto_complete, isAutoComplete);
        previousColor = attributes.getColor(R.styleable.core_area_previous_color, previousColor);
        underlineColor = attributes.getColor(R.styleable.core_area_underline_color, underlineColor);
        underlineSelectedColor = attributes.getColor(R.styleable.core_area_underline_selected_color, underlineSelectedColor);
        underlineAmount = attributes.getInt(R.styleable.core_area_codes, underlineAmount);
        textColor = attributes.getColor(R.styleable.core_area_text_color, textColor);
        line = attributes.getBoolean(R.styleable.core_area_line, line);
        inputType = attributes.getString(R.styleable.core_area_input_type);
        textSize = attributes.getFloat(R.styleable.core_area_text_size, textSize);
        int textSizePixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                textSize, getResources().getDisplayMetrics());

//        underlineWidth = attributes.getFloat(R.styleable.core_area_underline_width, underlineWidth);
//        int underlineWidthPixel= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                underlineWidth, getResources().getDisplayMetrics());

        circleRadius = attributes.getDimension(R.styleable.core_area_circle_radius, circleRadius);

        underlineWidth = attributes.getDimension(R.styleable.core_area_underline_width, underlineWidth);

        underlineStrokeWidth = attributes.getDimension(R.styleable.core_area_underline_stroke_width, underlineStrokeWidth);

        textMarginBottom = attributes.getDimension(R.styleable.core_area_text_margin_bottom, textMarginBottom);

        codeInput.setAutoComplete(isAutoComplete);
        codeInput.setPreviousColor(previousColor);
        codeInput.setUnderlineColor(underlineColor);
        codeInput.setUnderlineSelectedColor(underlineSelectedColor);
        codeInput.setTextColor(textColor);
        codeInput.setShape(line);
        codeInput.setUnderlineAmount(underlineAmount);
        codeInput.setTextSize(textSizePixel);
        codeInput.setUnderlineWidth(underlineWidth);
        codeInput.setCircleRadius(circleRadius);
        codeInput.setUnderlineStrokeWidth(underlineStrokeWidth);
        codeInput.setTextMarginBottom(textMarginBottom);
        attributes.recycle();
        codeInput.initDataStructures();
        codeInput.initPaint();
        codeInput.setInputType(inputType);
        codeInput.setRender(true);
    }


    private void init(Context context, AttributeSet attributes) {

        codeInput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fakeEdittext.clearFocus();
                fakeEdittext.requestFocus();

                showKeyboard();

            }
        });


        codeInput.setOnTextStackFull(this);
        setOnTextChanged(codeInput);
        fakeEdittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(codeInput.getUnderlineAmount())});
        fakeEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (start < codeInput.getUnderlineAmount() && start >= 0) {
                    if (!fakeEdittext.equals("")) {
                        listener.onEditTextChanged(s, start, count);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

    }

    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) fakeEdittext.getContext().getSystemService
                (Context.INPUT_METHOD_SERVICE);
        // the public methods don't seem to work for me, so… reflection.
        try {
            Method showSoftInputUnchecked = InputMethodManager.class.getMethod(
                    "showSoftInputUnchecked", int.class, ResultReceiver.class);
            showSoftInputUnchecked.setAccessible(true);
            showSoftInputUnchecked.invoke(imm, 0, null);
        } catch (Exception e) {
            // ho hum
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) fakeEdittext.getContext().getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(fakeEdittext.getWindowToken(), 0);
    }

    public void setOnTextChanged(onEdittextChanged listener) {

        this.listener = listener;
    }

    @Override
    public void onTextFull(String s, int i) {

        codeInputCompletedListener.onCodeInputCompleted(s);
        if (i == 0) {
            fakeEdittext.setText("");
        }
        hideKeyboard();
    }

    @Override
    public void onTextNotFull(String s) {
        codeInputCompletedListener.onCodeInputNotCompleted(s);

    }

    public void setOnTextCompleted(onCodeInputCompleted listener) {
        this.codeInputCompletedListener = listener;
    }

    public void setInflater(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void setAutoComplete(boolean autoComplete) {
        isAutoComplete = autoComplete;
    }

    public void setPreviousColor(int previousColor) {
        this.previousColor = previousColor;
    }

    public void setUnderlineAmount(int underlineAmount) {
        this.underlineAmount = underlineAmount;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
    }

    public void setUnderlineSelectedColor(int underlineSelectedColor) {
        this.underlineSelectedColor = underlineSelectedColor;
    }

    public onKeyEvent getOnKeyEventListener() {
        return onKeyEventListener;
    }

    public void setOnKeyEventListener(onKeyEvent onKeyEventListener) {
        this.onKeyEventListener = onKeyEventListener;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setLine(Boolean line) {
        this.line = line;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setUnderlineWidth(float underlineWidth) {
        this.underlineWidth = underlineWidth;
    }

    public void clearText() {
        fakeEdittext.setText("");

    }

    public void setText(CharSequence s) {
        fakeEdittext.setText(s);

    }

    public void requestCodeInputFocus() {
        fakeEdittext.clearFocus();
        fakeEdittext.requestFocus();
    }

    public void setString(String input) {
        if (input != null) {
            if (input.length() == this.codeInput.getStackSize()) {

                for (int i = 0; i < input.length(); i++) {
                    this.fakeEdittext.append(String.valueOf(input.charAt(i)));
                }
            }
        } else {
            this.fakeEdittext.setText("");
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            onKeyEventListener.onPressKey(event);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            onKeyEventListener.onPressKey(event);
        }
        return super.dispatchKeyEventPreIme(event);
    }
}
