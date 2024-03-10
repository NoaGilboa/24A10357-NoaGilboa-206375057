package com.example.HWMobile_NoaGilboa;

import android.widget.GridLayout;
import android.widget.ImageView;

public abstract class GameObject {

    private ImageView view;

    public GameObject(ImageView view) {
        this.view = view;
    }

    public ImageView getView() {
        return view;
    }

    public void setView(ImageView view) {
        this.view = view;
    }


    public void setPos(int row, int col) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(col, 1, 1);
        params.rowSpec = GridLayout.spec(row, 1, 1);
        params.height = 0;
        params.width = 0;
        view.setLayoutParams(params);
    }
}
