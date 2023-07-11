package com.example.todolist.tasks;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ScrollChildSwipeRefresh extends SwipeRefreshLayout {
    private View scrollUpChild;

    public ScrollChildSwipeRefresh(@NonNull Context context) {
        super(context);
    }

    public ScrollChildSwipeRefresh(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {
        if (scrollUpChild != null) {
            return ViewCompat.canScrollVertically(scrollUpChild, -1);
        }
        return super.canChildScrollUp();
    }

    public void setScrollUpChild(View view) {
        scrollUpChild = view;
    }
}
