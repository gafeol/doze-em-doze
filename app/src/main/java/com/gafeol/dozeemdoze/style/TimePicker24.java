package com.gafeol.dozeemdoze.style;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TimePicker;

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
