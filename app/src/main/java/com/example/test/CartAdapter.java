package com.example.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Product> products;
    private CartActivity cartActivity;
    private OnQuantityChangeListener quantityChangeListener;
    private OnRemoveListener removeListener;

    public CartAdapter(List<Product> products, OnQuantityChangeListener quantityChangeListener, OnRemoveListener removeListener) {
        this.products = products;
        this.cartActivity = cartActivity;
        this.quantityChangeListener = quantityChangeListener;
        this.removeListener = removeListener;
    }

    void removeItem(int position) {
        // Удаляем товар из списка
        products.remove(position);
        notifyItemRemoved(position);
        // Обновляем данные в корзине и интерфейсе
        cartActivity.updateTotalAmount();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productQuantity;
        Button btnIncrease;
        Button btnDecrease;
        // Button btnRemove;

        CartViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
           // btnRemove = itemView.findViewById(R.id.btn_remove);
        }

        void bind(Product product) {
            Glide.with(itemView.getContext()).load(product.getImageUrl()).into(productImage);
            productName.setText(product.getName());
            productPrice.setText(String.format("%.2f ₽", product.getPrice()));
            productQuantity.setText(String.valueOf(product.getQuantity()));

            btnIncrease.setOnClickListener(v -> {
                product.setQuantity(product.getQuantity() + 1);
                quantityChangeListener.onQuantityChange(product);
                notifyItemChanged(getAdapterPosition());
            });

            btnDecrease.setOnClickListener(v -> {
                if (product.getQuantity() > 1) {
                    product.setQuantity(product.getQuantity() - 1);
                    quantityChangeListener.onQuantityChange(product);
                    notifyItemChanged(getAdapterPosition());
                }
            });

           // btnRemove.setOnClickListener(v -> removeListener.onRemove(product));
        }
    }

    public interface OnQuantityChangeListener {
        void onQuantityChange(Product product);
    }

    public interface OnRemoveListener {
        void onRemove(Product product);
    }
}