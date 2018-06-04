package com.rpn_master.android.rpn_calculator;


import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Stack;

/**
 * Created by Prathmesh on 10/09/2017.
 */

public class Stacking implements Serializable{

    private static final int Length = 32;
    private static final int LenghtX4 = 128;
    private static final int innerScale = 32;


    private final Stack<BigDecimal> stack;
    private int scale = 2;

    public Stacking() {
        super();
        this.stack = new Stack<BigDecimal>();
    }

    public void push(final String number) {
        final BigDecimal newNo = new BigDecimal(number);
        this.stack.push(newNo);
    }

    public boolean isEmpty() {
        return this.stack.isEmpty();
    }

    public StringBuilder toString(final int levels) {
        final StringBuilder result = new StringBuilder(LenghtX4);
        if (this.stack != null) {
            final int depth = this.stack.size();
            for (int i = 0; i < levels; i++) {
                if (i != 0) {
                    result.append('\n');
                }
                final int idx = depth - levels + i;
                if (idx >= 0) {
                    result.append(formatNumber(this.stack.get(idx)));
                }
            }
        }
        return result;
    }
    @Override
    public String toString() {
        return this.toString(1).toString().replaceAll(",", "");
    }

    private String formatNumber(final BigDecimal number) {
        final StringBuilder result = new StringBuilder(Length);
        result.append(number.setScale(this.scale,
                RoundingMode.HALF_UP).toPlainString());
        if (this.scale > 0) {
            if (result.indexOf(".") == -1) {
                result.append('.');
            }
            final int zerosAfterPoint = result.length() - result.indexOf(".") - 1;
            for (int i = zerosAfterPoint; i < this.scale; i++) {
                result.append('0');
            }
        }

        int dot = result.indexOf(".");
        if (dot < 1) {
            dot = result.length();
        }
        int lowIndex = 0;
        if (result.charAt(0) == '-') {
            lowIndex = 1;
        }
        for (int i = dot - 3; i > lowIndex; i -= 3) {
            result.insert(i, ',');
        }
        return result.toString();
    }
    public void dop() {
        if (!this.stack.isEmpty()) {
            final BigDecimal topnum = this.stack.peek();
            this.stack.push(topnum);
        }
    }



    public void add() {
        if (this.stack.size() > 1) {
            final BigDecimal x = this.stack.pop();
            final BigDecimal y = this.stack.pop();
            final BigDecimal r = y.add(x);
            this.stack.push(r);
        }
    }


    public void subtract() {
        if (this.stack.size() > 1) {
            BigDecimal x = this.stack.pop();
            BigDecimal y = this.stack.pop();
            BigDecimal r = y.subtract(x);
            this.stack.push(r);
        }
    }

    public void multiply() {
        if (this.stack.size() > 1) {
            BigDecimal x = this.stack.pop();
            BigDecimal y = this.stack.pop();
            BigDecimal r = y.multiply(x);
            this.stack.push(r);
        }
    }

    public String divide() {
        String result = null;
        if (this.stack.size() > 1) {
            BigDecimal x = this.stack.pop();
            BigDecimal y = this.stack.pop();

            try {
                BigDecimal r = y.divide(x, innerScale,
                        RoundingMode.HALF_EVEN);
                this.stack.push(r);
            } catch (ArithmeticException e) {
                result = e.getMessage();
            }
        }
        return result;
    }

    public void setScale(final int newscale) {
        this.scale = newscale;
    }

    public void setScale() {
        if (!this.stack.isEmpty()) {
            BigDecimal x = this.stack.pop();
            int sc = x.intValue();
            if (sc < innerScale) {
                setScale(sc);
            }
        }
    }
    public int getScale() {return this.scale;}
}
