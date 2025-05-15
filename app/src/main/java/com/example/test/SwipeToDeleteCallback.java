package com.example.test;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private CartAdapter adapter;
    private Paint paint = new Paint();

    public SwipeToDeleteCallback(CartAdapter adapter) {
        super(0, ItemTouchHelper.LEFT);
        this.adapter = adapter;
        paint.setColor(Color.RED); // Цвет фона для кнопки удаления
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false; // Не обрабатываем движение
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            adapter.removeItem(position);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c,
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX,
                            float dY,
                            int actionState,
                            boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        float buttonWidth = 94 * recyclerView.getContext().getResources().getDisplayMetrics().density; // Ширина кнопки в пикселях
        float buttonHeight = itemView.getHeight(); // Высота кнопки равна высоте элемента

        // Если пользователь свайпнул влево
        if (dX < 0) {
            float buttonLeft = itemView.getRight() + dX - buttonWidth; // Сдвигаем кнопку влево
            float buttonTop = itemView.getTop();
            float buttonRight = itemView.getRight() + dX;
            float buttonBottom = itemView.getBottom();

            // Ограничиваем сдвиг кнопки
            if (buttonLeft < itemView.getRight() - buttonWidth) {
                buttonLeft = itemView.getRight() - buttonWidth;
                buttonRight = itemView.getRight();
            }

            // фон для кнопки удаления
            paint.setColor(Color.parseColor("#FEE7EB")); // Цвет фона для кнопки удаления
            c.drawRect(buttonLeft, buttonTop, buttonRight, buttonBottom, paint);

            // текст "Удалить"
            paint.setColor(Color.parseColor("#FE113C")); // Цвет текста
            paint.setTextSize(11 * recyclerView.getContext().getResources().getDisplayMetrics().scaledDensity);
            String text = "Удалить";
            float textWidth = paint.measureText(text);
            float textX = buttonLeft + (buttonWidth - textWidth) / 2; // текст по горизонтали
            float textY = buttonTop + (buttonHeight / 2) + (paint.getTextSize() / 2.5f); // текст по вертикали

            c.drawText(text, textX, textY, paint);
        }

        if (isCurrentlyActive && dX < -buttonWidth) {
            dX = -buttonWidth;
        }
        itemView.setTranslationX(dX);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

}
