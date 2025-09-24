package com.example.afinal;

import android.widget.ImageView;

public class ImageShapeHandler {

    private MainActivity mainActivity;

    // Конструктор для инициализации ссылающейся активности
    public ImageShapeHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    // Устанавливаем форму изображения как прямоугольную
    public void setRectangleShape() {
        mainActivity.setImageShape(false);  // Передаем false для прямоугольной формы
    }

    // Устанавливаем форму изображения как круглую
    public void setCircleShape() {
        mainActivity.setImageShape(true);   // Передаем true для круглой формы
    }
}
