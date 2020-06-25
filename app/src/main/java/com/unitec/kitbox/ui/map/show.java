package com.unitec.kitbox.ui.map;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.unitec.kitbox.R;

public class show extends ConstraintLayout {

    public TextView getT0() {
        return t0;
    }

    public void setT0(TextView t0) {
        this.t0 = t0;
    }

    private TextView t0,t1,t2,t3,t4,t5,t6;
    private View child;

    public ImageView getI0() {
        return i0;
    }

    private ImageView i0;
    public show(Context context) {
        this(context,null);
    }
    public show(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }
//    public void inputData(int column, String s,boolean highlight,boolean hot) {
//        int color = Color.WHITE;
//
//        if(highlight){
//            color = Color.CYAN;
//        } else {
//            if(hot) {
//                color = Color.RED;
//            } else {
//                if (column % 2 == 1) {
//
//                    color = 0XE0E0E0F0;
//                } else {
//                    color = 0xF0E0F0E0;
//                }
//            }
//        }
//
////                System.out.println("**********************   "+column+" *******************");
////                System.out.println("**********************   "+highlight+" *******************");
////                System.out.println("**********************   "+s+" *******************");
//        if(column ==1) {
//            t1.setText(s);
//            t1.setBackgroundColor(color);
//        } else {
//            if (column == 2) {
//                t2.setText(s);
//                t2.setBackgroundColor(color);
//            } else {
//                if (column == 3) {
//                    t3.setText(s);
//                    t3.setBackgroundColor(color);
//                } else {
//                    if (column == 4) {
//                        t4.setText(s);
//                        t4.setBackgroundColor(color);
//                    } else {
//                        if (column == 5) {
//                            t5.setText(s);
//                            t5.setBackgroundColor(color);
//                        } else {
//                            if (column == 0) {
//                                t0.setText(s);
//                                t0.setBackgroundColor(color);
//                            }
//                        }
//                    }
//
//                }
//            }
//        }
//
//    }
    public show(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        child = View.inflate(getContext(), R.layout.show,null);

        addView(child);
        t0 = (TextView) findViewById(R.id.t0);
        t1 = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);
        t3 = (TextView) findViewById(R.id.t3);
        t4 = (TextView) findViewById(R.id.t4);
        t5 = (TextView) findViewById(R.id.t5);
        t6 = (TextView) findViewById(R.id.t6);
        i0 = (ImageView) findViewById(R.id.i0);

    }
    public void fill(String s, String c, String l){
        t2.setText(s);
        t4.setText(c);
        t6.setText(l);
    }
}