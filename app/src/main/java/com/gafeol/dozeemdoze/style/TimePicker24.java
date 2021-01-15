package com.gafeol.dozeemdoze.style;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TimePicker;

/*
   Defines a custom Time Picker widget that has the 24-hour setting by default.
 */
public class TimePicker24 extends TimePicker {
        public TimePicker24(Context context) {
            super(context);
            init();
        }

        public TimePicker24(Context context,
                                 AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public TimePicker24(Context context,
                                 AttributeSet attrs,
                                 int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        private void init() {
            setIs24HourView(true);
        }
}
