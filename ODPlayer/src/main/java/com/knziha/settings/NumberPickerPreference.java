package com.knziha.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.knziha.ODPlayer.CMN;
import com.knziha.ODPlayer.R;
import com.knziha.filepicker.view.HorizontalNumberPicker;
import com.knziha.filepicker.view.NumberKicker;


/**
 * A preference type that allows a user to choose a pick a number
 *
 * @author KnIfER
 */
public class NumberPickerPreference extends Preference implements Preference.OnPreferenceClickListener
															,NumberKicker.OnValueChangeListener
{
    HorizontalNumberPicker mNP1;
    private int mValue = 0;
    private int mMinValue = 0;
    private int mMaxValue = 10;

    public NumberPickerPreference(Context context) {
        super(context);
        init(context, null);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        String stringVal = a.getString(index);
		CMN.Log("onGetDefaultValue", stringVal, a.getInt(index, 0));
        if (stringVal != null) {
            return Integer.valueOf(stringVal);
        } else {
            return a.getInt(index, 0);
        }
    }

	@Override
	public void onValueChange(NumberKicker picker, int oldVal, int value) {
		mValue = value;
		if(isPersistent())
			persistInt(mValue);
		notifyChanged();
		callChangeListener(value);
	}

	@Override
	protected void onSetInitialValue(@Nullable Object defaultValue) {
    	CMN.Log("onSetInitialValue", defaultValue, getPersistedInt(mValue));
		onNumberSelected(getPersistedInt(mValue));
	}

	private void onNumberSelected(Integer value) {
		if (isPersistent()) {
			persistInt(value);
		}
		mValue = value;
		try {
			getOnPreferenceChangeListener().onPreferenceChange(this, value);
		} catch (NullPointerException ignored) {}
	}

	private void init(Context context, AttributeSet attrs) {
        setOnPreferenceClickListener(this);
        if (attrs != null) {
			mMinValue = attrs.getAttributeIntValue(null, "npv_minValue", 0);
			mMaxValue = attrs.getAttributeIntValue(null, "npv_maxValue", 10);
        }
    }

	@Override
	public void onBindViewHolder(PreferenceViewHolder holder) {
		super.onBindViewHolder(holder);
		View mView = holder.itemView;
		LinearLayout widgetFrameView = mView.findViewById(android.R.id.widget_frame);
		mNP1=mView.findViewById(R.id.numberpicker);
		if (widgetFrameView == null || mNP1==null) return;
		mNP1.setOnValueChangedListener(null);
		mNP1.setMinValue(mMinValue); mNP1.setMaxValue(mMaxValue); mNP1.setValue(mValue);
		mNP1.setOnValueChangedListener(this);
		mNP1.setVerticalScrollBarEnabled(false);
		widgetFrameView.setVisibility(View.VISIBLE);
		widgetFrameView.setPadding(0,0,(int) (mView.getResources().getDisplayMetrics().density * 8),0);
	}

    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (mNP1 == null) {
            return superState;
        }
        final SavedState myState = new SavedState(superState);
        //if(mDialog.getDialog()!=null) myState.dialogBundle = mDialog.getDialog().onSaveInstanceState();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !(state instanceof SavedState)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
    }

	private static class SavedState extends BaseSavedState {
        Bundle dialogBundle;

        public SavedState(Parcel source) {
            super(source);
            dialogBundle = source.readBundle();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeBundle(dialogBundle);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @SuppressWarnings("unused")
        public static final Creator<SavedState> CREATOR =
                new Creator<NumberPickerPreference.SavedState>() {
                    public NumberPickerPreference.SavedState createFromParcel(Parcel in) {
                        return new NumberPickerPreference.SavedState(in);
                    }

                    public NumberPickerPreference.SavedState[] newArray(int size) {
                        return new NumberPickerPreference.SavedState[size];
                    }
                };
    }
}