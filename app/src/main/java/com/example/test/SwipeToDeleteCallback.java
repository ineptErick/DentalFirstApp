package com.example.test;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
        adapter.removeItem(position); // Удаляем элемент из адаптера
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

        if (dX < 0) { // Свайп влево
            // Рисуем фон для кнопки удаления
            float buttonWidth = 200; // Ширина кнопки
            float buttonHeight = itemView.getHeight(); // Высота кнопки

            // Определяем координаты для рисования кнопки
            float buttonLeft = itemView.getRight() + dX - buttonWidth;
            float buttonTop = itemView.getTop();
            float buttonRight = itemView.getRight() + dX;
            float buttonBottom = itemView.getBottom();

            // Рисуем фон кнопки
            paint.setColor(Color.RED); // Цвет фона для кнопки удаления
            c.drawRect(buttonLeft, buttonTop, buttonRight, buttonBottom, paint);

            // Рисуем текст "Удалить"
            paint.setColor(Color.WHITE); // Цвет текста
            paint.setTextSize(40); // Размер текста
            String text = "Удалить";
            float textWidth = paint.measureText(text);
            float textX = buttonLeft + (buttonWidth - textWidth) / 2; // Центрируем текст по горизонтали
            float textY = buttonTop + (buttonHeight / 2) + (paint.getTextSize() / 2.5f); // Центрируем текст по вертикали

            c.drawText(text, textX, textY, paint);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
