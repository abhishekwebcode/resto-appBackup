package com.loftysys.starbites.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.AutoCompleteTextView;

public class AutoDrop extends AppCompatAutoCompleteTextView {

        public AutoDrop(Context context) {
            super(context);
        }

        public AutoDrop(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public AutoDrop(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
            final InputConnection ic = super.onCreateInputConnection(outAttrs);
            if (ic != null && outAttrs.hintText == null) {
                // If we don't have a hint and our parent is a TextInputLayout, use it's hint for the
                // EditorInfo. This allows us to display a hint in 'extract mode'.
                final ViewParent parent = getParent();
                if (parent instanceof TextInputLayout) {
                    outAttrs.hintText = ((TextInputLayout) parent).getHint();
                }
            }
            return ic;
        }

    @Override
    public int getThreshold() {
        return 0;
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }
}
