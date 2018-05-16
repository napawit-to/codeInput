package com.github.glomadrian.codeinputlib;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.github.glomadrian.codeinputlib.data.FixedStack;
import com.github.glomadrian.codeinputlib.model.Underline;
import com.github.glomadrian.codeinputlib.model.onEdittextChanged;
import com.github.glomadrian.codeinputlib.model.onTextMaxSize;

import java.util.regex.Pattern;

/**
 * @author Adrián García Lomas
 */
public class CodeInput extends View implements onEdittextChanged {
    public onTextMaxSize listener = null;
    private static final int DEFAULT_CODES = 6;
    private static final Pattern KEYCODE_PATTERN = Pattern.compile("KEYCODE_(\\w)");
    private FixedStack<Character> characters;
    private Boolean line;
    private float circleRadius;
    private onCodeInputCompleted codeInputCompletedListener = null;

    public void setRender(Boolean render) {
        this.render = render;
    }

    private Boolean render = false;
    private Underline underlines[];

    public int getUnderlineAmount() {
        return underlineAmount;
    }

    private Paint underlinePaint;
    private Paint underlineSelectedPaint;
    private Paint PreviousUnderlineSelectedPaint;
    public boolean isAutoComplete;
    private Paint textPaint;
    private Paint hintPaint;
    private ValueAnimator reductionAnimator;
    private ValueAnimator hintYAnimator;

    private String inputType;
    private ValueAnimator hintSizeAnimator;
    private float underlineReduction;
    private float underlineStrokeWidth;
    private float underlineWidth;

    public void setUnderlineWidth(float underlineWidth) {
        this.underlineWidth = underlineWidth;
    }

    private float reduction;
    public int leftSize;
    private float textSize;
    private float textMarginBottom;
    private float hintX;
    private float hintNormalSize;
    private float hintSmallSize;
    private float hintMarginBottom;
    private float hintActualMarginBottom;
    private float viewHeight;
    private long animationDuration;
    private int height;
    private int previousColor;
    private int underlineAmount;
    private int underlineColor;
    private int underlineSelectedColor;
    private int hintColor;

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    private int textColor;
    private boolean underlined = true;
    private String hintText;


    public CodeInput(Context context) {
        super(context);

        init(null);
    }


    public CodeInput(Context context, AttributeSet attributeset) {
        super(context, attributeset);

        init(attributeset);

    }


    public void setOnTextStackFull(onTextMaxSize listener) {

        this.listener = listener;
    }

    public CodeInput(Context context, AttributeSet attributeset, int defStyledAttrs) {
        super(context, attributeset, defStyledAttrs);

        init(attributeset);
    }

    public void init(AttributeSet attributeset) {
        initDefaultAttributes();
        initCustomAttributes(attributeset);
        initDataStructures();
        initPaint();
        initAnimator();
        initViewOptions();
        startAnimation();
    }

    private void initDefaultAttributes() {
        previousColor = getContext().getResources().getColor(R.color.underline_default_color);
        isAutoComplete = false;
        underlineStrokeWidth = getContext().getResources().getDimension(R.dimen.underline_stroke_width);
        underlineWidth = getContext().getResources().getDimension(R.dimen.underline_width);
        underlineReduction = getContext().getResources().getDimension(R.dimen.section_reduction);
        textSize = getContext().getResources().getDimension(R.dimen.text_size);
        circleRadius = getContext().getResources().getDimension(R.dimen.circle_radius);
        textMarginBottom = getContext().getResources().getDimension(R.dimen.text_margin_bottom);
        underlineColor = getContext().getResources().getColor(R.color.underline_default_color);
        underlineSelectedColor = getContext().getResources().getColor(R.color.underline_selected_color);
        hintColor = getContext().getResources().getColor(R.color.hintColor);
        textColor = getContext().getResources().getColor(R.color.textColor);
        hintMarginBottom = getContext().getResources().getDimension(R.dimen.hint_margin_bottom);
        hintNormalSize = getContext().getResources().getDimension(R.dimen.hint_size);
        hintSmallSize = getContext().getResources().getDimension(R.dimen.hint_small_size);
        animationDuration = getContext().getResources().getInteger(R.integer.animation_duration);
        viewHeight = getContext().getResources().getDimension(R.dimen.view_height);
        hintX = 0;
        hintActualMarginBottom = 0;
        underlineAmount = DEFAULT_CODES;
        reduction = 0.0F;
        line = false;
        inputType = "character";
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setUnderlineSelectedColor(int underlineSelectedColor) {
        this.underlineSelectedColor = underlineSelectedColor;
    }

    public float getUnderlineStrokeWidth() {
        return underlineStrokeWidth;
    }

    public void setUnderlineStrokeWidth(float underlineStrokeWidth) {
        this.underlineStrokeWidth = underlineStrokeWidth;
    }

    public float getTextMarginBottom() {
        return textMarginBottom;
    }

    public void setTextMarginBottom(float textMarginBottom) {
        this.textMarginBottom = textMarginBottom;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
    }

    public void setUnderlineAmount(int underlineAmount) {
        this.underlineAmount = underlineAmount;
    }

    public void setPreviousColor(int previousColor) {
        this.previousColor = previousColor;
    }

    public void setShape(Boolean shape) {
        this.line = shape;
    }

    public void setHintColor(int hintColor) {
        this.hintColor = hintColor;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    public void setAutoComplete(boolean autoComplete) {
        isAutoComplete = autoComplete;
    }

    public float getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(float circleRadius) {
        this.circleRadius = circleRadius;
    }

    private void initCustomAttributes(AttributeSet attributeset) {
        TypedArray attributes =
                getContext().obtainStyledAttributes(attributeset, R.styleable.core_area);
        isAutoComplete = attributes.getBoolean(R.styleable.core_area_auto_complete, isAutoComplete);
        previousColor = attributes.getColor(R.styleable.core_area_previous_color, previousColor);
        underlineColor = attributes.getColor(R.styleable.core_area_underline_color, underlineColor);
        underlineSelectedColor = attributes.getColor(R.styleable.core_area_underline_selected_color, underlineSelectedColor);
        hintColor = attributes.getColor(R.styleable.core_area_underline_color, hintColor);
        hintText = attributes.getString(R.styleable.core_area_hint_text);
        underlineAmount = attributes.getInt(R.styleable.core_area_codes, underlineAmount);
        textColor = attributes.getInt(R.styleable.core_area_text_color, textColor);
        line = attributes.getBoolean(R.styleable.core_area_line, line);
        inputType = attributes.getString(R.styleable.core_area_input_type);
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public void initDataStructures() {
        underlines = new Underline[underlineAmount];
        characters = new FixedStack();
        characters.setMaxSize(underlineAmount);
        leftSize = 0;
    }

    public void initPaint() {
        underlinePaint = new Paint();
        underlinePaint.setColor(underlineColor);
        underlinePaint.setStrokeWidth(underlineStrokeWidth);
        underlinePaint.setStyle(android.graphics.Paint.Style.STROKE);
        underlineSelectedPaint = new Paint();
        underlineSelectedPaint.setColor(underlineSelectedColor);
        underlineSelectedPaint.setStrokeWidth(underlineStrokeWidth);
        underlineSelectedPaint.setStyle(android.graphics.Paint.Style.STROKE);
        PreviousUnderlineSelectedPaint = new Paint();
        PreviousUnderlineSelectedPaint.setColor(previousColor);
        PreviousUnderlineSelectedPaint.setStrokeWidth(underlineStrokeWidth);
        PreviousUnderlineSelectedPaint.setStyle(android.graphics.Paint.Style.STROKE);
        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        hintPaint = new Paint();
        hintPaint = new Paint();
        hintPaint.setTextSize(hintNormalSize);
        hintPaint.setAntiAlias(true);
        hintPaint.setColor(underlineColor);
    }


    private void initAnimator() {
        reductionAnimator = ValueAnimator.ofFloat(0, underlineReduction);
        reductionAnimator.setDuration(animationDuration);
        reductionAnimator.addUpdateListener(new ReductionAnimatorListener());
        reductionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        hintSizeAnimator = ValueAnimator.ofFloat(hintNormalSize, hintSmallSize);
        hintSizeAnimator.setDuration(animationDuration);
        hintSizeAnimator.addUpdateListener(new HintSizeAnimatorListener());
        hintSizeAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        hintYAnimator = ValueAnimator.ofFloat(0, hintMarginBottom);
        hintYAnimator.setDuration(animationDuration);
        hintYAnimator.addUpdateListener(new HintYAnimatorListener());
        hintYAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    private void initViewOptions() {
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
//    if (!gainFocus && characters.size() == 0) {
//      reverseAnimation();
//    }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, (int) viewHeight, oldw, oldh);
        height = h;
        initUnderline();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), (int) viewHeight);
    }

    private void initUnderline() {
        for (int i = 0; i < underlineAmount; i++) {
            underlines[i] = createPath(i, underlineWidth);
        }
    }

    private Underline createPath(int position, float sectionWidth) {
        float fromX = sectionWidth * (float) position;
        return new Underline(fromX, height, fromX + sectionWidth, height);
    }

    private void showKeyboard() {
        InputMethodManager inputmethodmanager =
                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmethodmanager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);

    }

    private void startAnimation() {
        reductionAnimator.start();
        hintSizeAnimator.start();
        hintYAnimator.start();
        underlined = false;
    }

    private void reverseAnimation() {
        reductionAnimator.reverse();
        hintSizeAnimator.reverse();
        hintYAnimator.reverse();
        underlined = true;
    }

    /**
     * Detects the del key and delete the numbers
     */
//  @Override public boolean onKeyDown(int keyCode, KeyEvent keyevent) {
//    if (keyCode == KeyEvent.KEYCODE_DEL && characters.size() != 0) {
//      characters.pop();
//      leftSize--;
//    }
//    return super.onKeyDown(keyCode, keyevent);
//  }
//
//  /**
//   * Capture the keyboard events but only if are A-Z 0-9
//   */
//  @Override public boolean onKeyUp(int keyCode, KeyEvent keyevent) {
//    String text = KeyEvent.keyCodeToString(keyCode);
//    Matcher matcher = KEYCODE_PATTERN.matcher(text);
//    if (matcher.matches()) {
//      char character = matcher.group(1).charAt(0);
//      characters.push(character);
//      leftSize++;
//
//
//      if(leftSize==characters.getMaxSize() && isAutoComplete){
//        onStackFullAutoComplete();
//      }
//      if(leftSize>6){
//        leftSize=6;
//      }else {
//        if (leftSize == characters.getMaxSize() && !isAutoComplete) {
//
//           onStackFullWaitingOrder();
//        }
//      }
//      return true;
//    } else {
//
//      return false;
//    }
//  }
    private void onStackFullWaitingOrder() {


        if (listener != null) {
            listener.onTextFull(popStackToStringAndFill(), 1);

        }


    }

    private String popStackToStringAndFill() {
        FixedStack<Character> newStack = (FixedStack<Character>) characters.clone();
        String input = "";
        if (!characters.isEmpty()) {
            for (int i = 0; i < newStack.getMaxSize(); i++) {

                input += newStack.pop();

            }

        }
        return reverseString(input);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionevent) {
        if (motionevent.getAction() == 0) {
            requestFocus();
            if (underlined) {
                startAnimation();
            }

            //  showKeyboard();
        }
        return super.onTouchEvent(motionevent);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        for (int i = 0; i < underlines.length; i++) {
            Underline sectionpath = underlines[i];
            float fromX = sectionpath.getFromX() + reduction;
            float fromY = sectionpath.getFromY();
            float toX = sectionpath.getToX() - reduction;
            float toY = sectionpath.getToY();

            if (!line) {
                drawCircle(i, fromX, fromY, toX, toY, canvas);

            } else {
                drawSection(i, fromX, fromY, toX, toY, canvas);

            }


            if (characters.toArray().length > i && characters.size() != 0) {
                if (!line) {
                    drawCircleCharacter(fromX, toX, characters.get(i), canvas);

                } else {
                    if (inputType != null) {
                        if (inputType.equals("character")) {
                            drawCharacter(fromX, toX, characters.get(i), canvas);
                        }
                        if (inputType.equals("password")) {

                            drawCircleCharacterCenter(fromX, toX, characters.get(i), canvas);
                        }
                    } else {
                        drawCharacter(fromX, toX, characters.get(i), canvas);
                    }
                }
            }
        }
        if (hintText != null) {
            drawHint(canvas);
        }
        invalidate();

    }

    public void drawCircle(int position, float fromX, float fromY, float toX, float toY,
                           Canvas canvas) {
        float actualWidth = toX - fromX;
        float centerWidth = actualWidth / 2;
        float centerX = fromX + centerWidth;
        Paint paint = underlinePaint;
        if (position == characters.size() && !underlined) {
            paint = underlineSelectedPaint;
        }
        if ((characters.size() - 1) - position >= 0) {
            paint = PreviousUnderlineSelectedPaint;
        }


        canvas.drawCircle(centerX, height - textMarginBottom, circleRadius, paint);
        // canvas.drawLine(fromX, fromY, toX, toY, paint);
    }

    private void drawCircleCharacter(float fromX, float toX, Character character, Canvas canvas) {
        float actualWidth = toX - fromX;
        float centerWidth = actualWidth / 2;
        float centerX = fromX + centerWidth;
        canvas.drawCircle(centerX, height - textMarginBottom, circleRadius, textPaint);
    }

    private void drawCircleCharacterCenter(float fromX, float toX, Character character, Canvas canvas) {
        float actualWidth = toX - fromX;
        float centerWidth = actualWidth / 2;
        float centerX = fromX + centerWidth;
        canvas.drawCircle(centerX, height / 2, circleRadius, textPaint);
    }

    private void drawSection(int position, float fromX, float fromY, float toX, float toY,
                             Canvas canvas) {

        Paint paint = underlinePaint;
        if (position == characters.size() && !underlined) {
            paint = underlineSelectedPaint;
        }
        if ((characters.size() - 1) - position >= 0) {
            paint = PreviousUnderlineSelectedPaint;
        }


        canvas.drawLine(fromX, fromY, toX, toY, paint);
    }

    private void drawCharacter(float fromX, float toX, Character character, Canvas canvas) {
        float actualWidth = toX - fromX;
        float centerWidth = actualWidth / 2;
        float centerX = fromX + centerWidth;
        canvas.drawText(character.toString(), centerX, height - textMarginBottom, textPaint);
        // canvas.drawCircle(centerX, height - textMarginBottom,15,textPaint);
    }

    private void drawHint(Canvas canvas) {
        canvas.drawText(hintText, hintX, height - textMarginBottom - hintActualMarginBottom, hintPaint);
    }

    public Character[] getCode() {
        return characters.toArray(new Character[underlineAmount]);
    }

    public void onStackFullAutoComplete() {


        if (listener != null) {
            listener.onTextFull(popStackToString(), 0);
            leftSize = 0;

        }
    }

    public String popStackToString() {
        String input = "";
        if (!characters.isEmpty()) {
            for (int i = 0; i < characters.getMaxSize(); i++) {
                input += characters.pop();

            }

        }
        return reverseString(input);
    }

    public String reverseString(String s) {

        int i, len = s.length();
        StringBuilder dest = new StringBuilder(len);

        for (i = (len - 1); i >= 0; i--) {
            dest.append(s.charAt(i));
        }
        return dest.toString();
    }

    @Override
    public void onEditTextChanged(CharSequence c, int start, int count) {
        if (count == 0) {
            if (!characters.isEmpty()) {

                characters.pop();
                leftSize--;
                listener.onTextNotFull(c.toString());
            }
        } else if (count == characters.getMaxSize()) {

            for (int i = 0; i < characters.getMaxSize(); i++) {
                leftSize++;
                characters.push(c.charAt(i));
            }
            if (isAutoComplete) {
                onStackFullAutoComplete();
            }
            if (leftSize > underlineAmount) {
                leftSize = underlineAmount;
            } else {
                if (!isAutoComplete) {

                    onStackFullWaitingOrder();
                }
            }


        } else {

            char character = c.charAt(start);
            characters.push(character);
            leftSize++;
            if (leftSize == characters.getMaxSize() && isAutoComplete) {
                onStackFullAutoComplete();
            }
            if (leftSize > underlineAmount) {
                leftSize = underlineAmount;
            } else {
                if (leftSize == characters.getMaxSize() && !isAutoComplete) {

                    onStackFullWaitingOrder();
                }
            }

        }
    }

//  private boolean checkDelelte(int numblist) {
//    {
//      temp =numblist;
//      if(numblist)
//      return true;
//    }
//  }

    /**
     * Listener to update the reduction of the underline bars
     */
    private class ReductionAnimatorListener implements ValueAnimator.AnimatorUpdateListener {

        public void onAnimationUpdate(ValueAnimator valueanimator) {
            float value = ((Float) valueanimator.getAnimatedValue()).floatValue();
            reduction = value;
        }
    }

    /**
     * Listener to update the hint y values
     */
    private class HintYAnimatorListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            hintActualMarginBottom = (float) animation.getAnimatedValue();
        }
    }

    /**
     * Listener to update the size of the hint text
     */
    private class HintSizeAnimatorListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float size = (float) animation.getAnimatedValue();
            hintPaint.setTextSize(size);
        }
    }

    public void ShowSoftKeyBoard(EditText fakeEdittext) {

        if (underlined) {
            startAnimation();
        }
        fakeEdittext.requestFocus();
        fakeEdittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        showKeyboard();

    }

    public int getStackSize() {
        return characters.getMaxSize();
    }

}
