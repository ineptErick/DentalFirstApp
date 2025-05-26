package com.example.test;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnQuantityChangeListener, CartAdapter.OnRemoveListener {

    private CartAdapter adapter;
    private List<Product> productsInCart; // = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        // Инициализация RecyclerView и адаптера
        CartAdapter cartAdapter = new CartAdapter(productsInCart, this, this);


        productsInCart = loadProductsFromJson();
        goToCartScreen();
        loadCartData(); // Загрузка данных о товарах из SharedPreferences или другого источника
        adapter.notifyDataSetChanged();
    }

    private void goToCatalogueScreen(){
        setContentView(R.layout.catalogue_screen);
    }

    private void setButtonsBar(){

        findViewById(R.id.button_home).setOnClickListener(v -> {
            // Логика
        });
        findViewById(R.id.button_catalog).setOnClickListener(v -> {
            goToCatalogueScreen();
        });
        findViewById(R.id.button_cart).setOnClickListener(v -> {
            goToCartScreen();
        });
        findViewById(R.id.button_profile).setOnClickListener(v -> {
            // Логика
        });
        findViewById(R.id.button_actions).setOnClickListener(v -> {
            // Логика
        });

        Button buttonHome = findViewById(R.id.button_home);
        Button buttonCatalog = findViewById(R.id.button_catalog);
        Button buttonCart = findViewById(R.id.button_cart);
        Button buttonProfile = findViewById(R.id.button_profile);
        Button buttonActions = findViewById(R.id.button_actions);

        // "Главная"
        buttonHome.setOnClickListener(v -> {
            changeButtonImage(buttonHome, R.drawable.cart_button_main_blue, R.drawable.cart_button_main);
        });

        // "Каталог"
        buttonCatalog.setOnClickListener(v -> {
            changeButtonImage(buttonCatalog, R.drawable.cart_button_catalogue_blue, R.drawable.cart_button_catalogue);
            goToCatalogueScreen();
        });

        // "Корзина"
        buttonCart.setOnClickListener(v -> {
            changeButtonImage(buttonCart, R.drawable.cart_button_bucket_blue, R.drawable.cart_button_bucket);
            goToCartScreen();
        });

        // "Профиль"
        buttonProfile.setOnClickListener(v -> {
            changeButtonImage(buttonProfile, R.drawable.cart_button_profile_blue, R.drawable.cart_button_profile);
            // логика
        });

        // "Акции"
        buttonActions.setOnClickListener(v -> {
            changeButtonImage(buttonActions, R.drawable.cart_button_actions_blue, R.drawable.cart_button_actions);
            // логика
        });
    }

    // Метод для изменения изображения кнопки
    private void changeButtonImage(Button button, int activeImageResId, int defaultImageResId) {
        // Меняем изображение на синий аналог
        button.setCompoundDrawablesWithIntrinsicBounds(0, activeImageResId, 0, 0);

        // Возвращаем изображение обратно через 300 мс
        new Handler().postDelayed(() -> {
            button.setCompoundDrawablesWithIntrinsicBounds(0, defaultImageResId, 0, 0);
        }, 300);
    }

    private void goToCartScreen(){
        if(productsInCart == null || productsInCart.isEmpty()){
            // Если товаров у пользователя еще нет
            setContentView(R.layout.empty_cart);
            setButtonsBar();
            findViewById(R.id.btn_catalogue).setOnClickListener(v -> {
                goToCatalogueScreen();
            });
        }else {
            // Если у пользователя уже есть товары в корзине
            setContentView(R.layout.activity_cart);
            setButtonsBar();
            RecyclerView recyclerView = findViewById(R.id.recycler_view);

            // Инициализация адаптера
            adapter = new CartAdapter(productsInCart, this, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Установка обработчика свайпа
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
            itemTouchHelper.attachToRecyclerView(recyclerView);

            // Обновление текстовых полей с количеством товаров и общей суммой
            updateTotalAmount();

            findViewById(R.id.btn_checkout).setOnClickListener(v -> {
                // Логика оформления заказа (переход на следующий экран)
            });

            // кнопка id.clear_all_button по нажатию удаляет все товары из корзины
            findViewById(R.id.clear_all_button).setOnClickListener(v -> {
                clearCart();
                updateTotalAmount();
                goToCartScreen();
            });
        }
    }

    // Метод для получения товаров из корзины
    private List<Product> getProductsInCart() {
        return productsInCart;
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveCartData(); // Сохраняем данные при приостановке активности
    }

    @Override
    public void onQuantityChange() {
        updateTotalAmount();
    }

    @Override
    public void onRemove(Product product) {
        productsInCart.remove(product);
        adapter.notifyDataSetChanged();
        updateTotalAmount();
        goToCartScreen();
    }

    protected void updateTotalAmount() {
        TextView numberGoodsText = findViewById(R.id.number_goods_text);
        TextView numberGoods = findViewById(R.id.number_goods);
        TextView rublesText = findViewById(R.id.rubles_text);
        TextView totalAmount = findViewById(R.id.total_amount);

        double totalPrice = 0.0;
        int totalItems = 0; // Для подсчета общего количества товаров

        if(productsInCart != null && !productsInCart.isEmpty()){
            for (Product product : productsInCart) {
                totalPrice += product.getPrice() * product.getQuantity(); // Учитываем количество
                totalItems += product.getQuantity(); // Считаем общее количество товаров
            }
            numberGoodsText.setText(String.format("· %d продуктов", totalItems)); // Общее количество товаров
            numberGoods.setText(String.format("Товары (%d)", totalItems)); // Общее количество товаров
            rublesText.setText(String.format("· %.2f ₽", totalPrice)); // Общая сумма
            totalAmount.setText(String.format(" %.2f ₽", totalPrice)); // Общая сумма
        } else {
            numberGoodsText.setText("· 0");
            numberGoods.setText("Товары (0)");
            rublesText.setText("· 0 ₽");
            totalAmount.setText("· 0 ₽");
        }
    }

    private void clearCart() {
        productsInCart.clear(); // Очистка списка товаров в корзине
        updateTotalAmount();
        goToCartScreen(); // Обновление экрана корзины
    }

    private void saveCartData() {
        SharedPreferences sharedPreferences = getSharedPreferences("cart_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(productsInCart);
        editor.putString("cart_data", jsonString);
        editor.apply();
    }

    private void loadCartData() {
        SharedPreferences sharedPreferences = getSharedPreferences("cart_prefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonString = sharedPreferences.getString("cart_data", null);
        if (jsonString != null) {
            Type type = new TypeToken<List<Product>>() {}.getType();
            productsInCart = gson.fromJson(jsonString, type);
            adapter.notifyDataSetChanged();
        }
    }

    public List<Product> loadProductsFromJson() {
        List<Product> products = new ArrayList<>();
        try {
            InputStreamReader reader = new InputStreamReader(getAssets().open("tovary_i_kategorii.json"));
            Type productListType = new TypeToken<List<Product>>() {}.getType();
            products = new Gson().fromJson(reader, productListType);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
}