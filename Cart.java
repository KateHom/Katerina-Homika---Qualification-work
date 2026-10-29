package com.example.qualificationwork.models;

import java.util.HashMap;
import java.util.Map;

public class Cart {

    // Хранилище товаров в корзине: ключ — ID товара, значение — его количество
    // Storage of items in the cart: key is the product ID, value is its quantity
    private final Map<Long, Integer> items = new HashMap<>();

    // Получение текущего набора товаров в корзине
    // Getting the current set of items in the cart
    public Map<Long, Integer> getItems() {
        return items;
    }

    // эта функция позволяет уменьшить количество или удалить продукт в/из корзины
     // this function allows to decrease or delete the product in/from the cart
    public void removeProduct(Long productId) {
        if (items.containsKey(productId)) {
            int qty = items.get(productId);
            if (qty > 1) {
                items.put(productId, qty - 1);
            } else {
                items.remove(productId);
            }
        }
    }
}
