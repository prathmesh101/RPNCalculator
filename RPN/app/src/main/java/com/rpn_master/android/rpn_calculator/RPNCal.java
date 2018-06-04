package com.rpn_master.android.rpn_calculator;

import android.app.Activity;;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class RPNCal extends Activity{
    private Stacking stack;
    private String fault;
    private int lines;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Log.d("RPNCal", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadState();
    }


    public void changeDisplay() {
        final TextView dis = (TextView) findViewById(R.id.textDisplay);
        StringBuilder text;
        if (this.buffer.isEmpty() && this.fault == null) {
            if (this.stack.isEmpty()) {
                text = new StringBuilder();
                for (int i = 1; i < this.lines - 1; i++) {
                    text.append('\n');
                }
                text.append('0');
                final int scale = this.stack.getScale();
                if (scale > 0) {
                    text.append('.');
                    for (int i = 0; i < scale; i++) {
                        text.append('0');
                    }
                }
            } else {
                text = this.stack.toString(this.lines);
            }
        } else {
            text = this.stack.toString(this.lines - 1);
            text.append("\n");
            if (this.fault == null) {
                text.append(this.buffer.get());
            } else {
                text.append(this.fault);
                this.fault = null;
            }
        }
        dis.setLines(this.lines);
        dis.setText(text);
        scrollRight();
    }

    public void impPush() {
        if (!this.buffer.isEmpty()) {
            final String num = this.buffer.get();
            this.stack.push(num);
            this.buffer.zap();
        }
    }
    private void keyDelete() {

            this.buffer.delete();
        this.changeDisplay();
    }
    private void keyEnter() {
        if (this.buffer.isEmpty()) {
            this.stack.dop();
        } else {
            final String num = this.buffer.get();
            this.stack.push(num);
            this.buffer.zap();
        }
        this.changeDisplay();
    }

    private boolean keyOther(final char c) {
        boolean handled = false;
        switch (c) {
            case '+':
                impPush();
                this.stack.add();
                this.changeDisplay();
                handled = true;
                break;
            case '-':
                impPush();
                this.stack.subtract();
                this.changeDisplay();
                handled = true;
                break;
            case '*':
                impPush();
                this.stack.multiply();
                this.changeDisplay();
                handled = true;
                break;
            case '/':
                impPush();
                this.fault = this.stack.divide();
                this.changeDisplay();
                handled = true;
                break;
            default:
                if ((c >= '0' && c <= '9') || c == '.') {
                    this.buffer.append(c);
                    this.changeDisplay();
                    handled = true;
                }
        }
        return handled;
    }

    private void scrollRight() {
        ((HorizontalScrollView) findViewById(R.id.Scrolling)).post(new Runnable() {
            @Override
            public void run() {
                ((HorizontalScrollView) findViewById(R.id.Scrolling)).fullScroll(View.FOCUS_RIGHT);
            }
        });
    }
    public void clickHandler(final View v) {
        final String key = (String) v.getTag();
        if ("clear".equals(key)) {
            impPush();
            this.stack.setScale();
            changeDisplay();
        } else if ("back".equals(key)) {
            keyDelete();
        }  else if ("enter".equals(key)) {
            keyEnter();
        } else {
            final char c = key.charAt(0);
            keyOther(c);
        }
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        final TextView dis = (TextView) findViewById(R.id.textDisplay);
        final FrameLayout frameView = (FrameLayout) findViewById(R.id.MainFrame);
        this.lines = 1 + Math.round((float) frameView.getHeight() / (float) dis.getLineHeight());
        changeDisplay();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
        final File dir = getCacheDir();
        final File data = new File(dir,"stack");
        FileOutputStream fos = null;
        ObjectOutputStream out = null;

    }
    private void loadState() {
        final File dir = getCacheDir();
        final File data = new File(dir,"stack");
        FileInputStream fis = null;
        ObjectInputStream in = null;

        if (this.buffer == null) {
            this.buffer = new Input();
        }
        if (this.stack == null) {
            this.stack = new Stacking();
        }
    }


    private Input buffer;
    public class Input implements Serializable {

        private static final int startCap = 32;
        private final StringBuilder buffer = new StringBuilder(startCap);

        public Input(final String value) {
            super();
            this.set(value);
        }

        public Input() {

            super();
        }
        public void append(final char digit) {
            switch (digit) {
                case '.':
                    if (this.buffer.indexOf(".") == -1) {
                        if (this.buffer.length() == 0) {
                            this.buffer.append('0');
                        }
                        this.buffer.append('.');
                    }
                    break;
                case '0':
                    if (!"0".equals(this.buffer)) {
                        this.buffer.append('0');
                    }
                    break;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    this.buffer.append(digit);
                    break;
                default:
                    Log.e("append", "Ignoring character '" + digit + "'");
            }
        }


        public void delete() {
            final int len = this.buffer.length();
            if (len > 0) {
                this.buffer.setLength(len - 1);
            }
        }
        public void zap() {

            this.buffer.setLength(0);
        }
        public boolean isEmpty() {

            return this.buffer != null && this.buffer.length() == 0;
        }

        final public void set(final String value) {
            this.buffer.setLength(0);
            this.buffer.append(value);
        }

        public String get() {

            return this.buffer.toString();
        }
        @Override
        public String toString() {

            return this.get();
        }
    }

}


