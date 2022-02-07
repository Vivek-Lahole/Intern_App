package com.farmigo.app.utility;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatSpinner;

import java.lang.reflect.Method;

//public class MySpinner extends Spinner {
//    private int lastSelected = 0;
//
//    public MySpinner(Context context)
//    { super(context); }
//
//    public MySpinner(Context context, AttributeSet attrs)
//    { super(context, attrs); }
//
//    public MySpinner(Context context, AttributeSet attrs, int defStyle)
//    { super(context, attrs, defStyle); }
//
////    @Override
////    protected void onLayout(boolean changed, int l, int t, int r, int b) {
////        if(this.lastSelected == this.getSelectedItemPosition() && getOnItemSelectedListener() != null)
////            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), this.getSelectedItemPosition(), getSelectedItemId());
////        if(!changed)
////            lastSelected = this.getSelectedItemPosition();
////
////        super.onLayout(changed, l, t, r, b);
////    }
//}

public class MySpinner extends Spinner {

    private int lastSelected = 0;
    private static Method s_pSelectionChangedMethod = null;
    private Object ob = null;


    static {
        try {
            Class noparams[] = {};
            Class targetClass = AdapterView.class;

            s_pSelectionChangedMethod = targetClass.getDeclaredMethod("selectionChanged", noparams);
            if (s_pSelectionChangedMethod != null) {
                s_pSelectionChangedMethod.setAccessible(true);
            }

        } catch (Exception e) {
            Log.e("Custom spinner, reflection bug:", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public MySpinner(Context context) {
        super(context);
    }

    public MySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void testReflectionForSelectionChanged() {
        try {
            Class noparams[] = {};
            s_pSelectionChangedMethod.invoke(this, noparams);
        } catch (Exception e) {
            Log.e("Custom spinner, reflection bug: ", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent m) {
        if (m.getAction() == MotionEvent.ACTION_DOWN) {
            ob = this.getSelectedItem();
        }
        return super.onTouchEvent(m);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        if (this.getSelectedItem().equals(ob))
            testReflectionForSelectionChanged();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (this.lastSelected == this.getSelectedItemPosition())
            testReflectionForSelectionChanged();
        if (!changed)
            lastSelected = this.getSelectedItemPosition();

        super.onLayout(changed, l, t, r, b);
    }
}
