package com.example.HWMobile_NoaGilboa;

import android.content.Context;
import android.widget.ImageView;

public class Gift extends GameObject {

    public Gift(Context context) {
        super(new ImageView(context));
        getView().setImageResource(R.drawable.gift);
    }
}
