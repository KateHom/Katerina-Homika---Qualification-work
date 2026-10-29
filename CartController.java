package com.example.qualificationwork.controllers;

import com.example.qualificationwork.service.CartService;
import com.example.qualificationwork.models.Cart;
import com.example.qualificationwork.models.Product;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@SessionAttributes("cart")
// используется для хранения товаров в корзине
// used for storage items in cart

public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @ModelAttribute("cart")
    // для создания корзины для каждого нового клиента
    // for creating new cart for each new customer
    public Cart createCart() {
        return new Cart();
    }

    @PostMapping("/cart/add")
    //добавление товара в корзину
    // adding item to the cart
    public String addToCart(
            @RequestParam Long productId, // which item
            @ModelAttribute("cart") Cart cart, //to the cart
            RedirectAttributes redirectAttributes) {
        try {
            cartService.add(productId, cart);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/cart/remove")
    // удаление товара из корзины
     // delete the item from the cart
    public String removeFromCart(
            @RequestParam Long productId,
            @ModelAttribute("cart") Cart cart) {
        cartService.remove(productId, cart);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String showCart(
            @ModelAttribute("cart") Cart cart, Model model) {
        Map<Product, Integer> cartItems = new HashMap<>();
        BigDecimal totalPrice = new BigDecimal("0");

        // Проходим по всем элементам корзины для формирования списка и одновременного расчета суммы
        // Iterating through all cart items to build the items map and calculate the total price
        for (Map.Entry<Long, Integer> entry : cart.getItems().entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = cartService.getProductById(productId);

            // Проверяем, что товар реально существует в базе данных
            // Checking if the product actually exists in the database
            if (product != null) {
                cartItems.put(product, quantity);

                // Конвертируем цену в BigDecimal и считаем сумму для данной позиции
                // Converting product price to BigDecimal and calculating item subtotal
                BigDecimal price = BigDecimal.valueOf(product.getPrice());
                    BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(quantity));
                    totalPrice = totalPrice.add(itemTotal);
            }
        }

        model.addAttribute("cartItems", cartItems);

// Считаем суммарное количество всех единиц товаров для отображения счетчика
        // Calculating total count of all item units for UI badge display
        int cartCount = cart.getItems().values().stream().mapToInt(Integer::intValue).sum();
        model.addAttribute("cartCount", cartCount);
        model.addAttribute("totalPrice", totalPrice);

        return "website/cart";
    }
    @GetMapping("/cart/increase")
    // возможность добавить товар кнопкой в корзине
    // option in the cart increase item
    public String increaseQuantity(@RequestParam Long productId,
                                   @ModelAttribute("cart") Cart cart,
                                   RedirectAttributes redirectAttributes) {
        try {
            cartService.add(productId, cart);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cart";
    }
    @GetMapping("/cart/decrease")
    // уменьшить товар в корзине
     // decrease item in the cart
    public String decreaseQuantity(@RequestParam Long productId,
                                   @ModelAttribute("cart") Cart cart) {
        cartService.removeOne(productId, cart); // удаляем только 1 шт
        return "redirect:/cart";
    }

    // Обработка клика по кнопке "Оформить заказ"
    // button checkout
    @PostMapping("/cart/checkout")
    public String checkout(
            @RequestParam String customerName,
            @RequestParam String customerEmail,
            @ModelAttribute("cart") Cart cart,
            Model model) {

        // Если корзина пустая — отправляем обратно
        // if cart is empty - redirect back
        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        try {
            // Вызываем сервис для списания со склада и создания заказа
            // call the service to write off the goods from the warehouse and create an order
            cartService.checkout(cart, customerName, customerEmail);

            // Очищаем корзину после успешного заказа
            // empty the cart after is was checkout
            cart.getItems().clear();

            return "redirect:/order-success";
        } catch (IllegalArgumentException e) {
            // Если товара не хватило на складе
            // If item was not enough on the stock
            model.addAttribute("errorMessage", e.getMessage());
            return showCart(cart, model);
        }
    }
    @GetMapping("/order-success")
    public String orderSuccess() {
        return "website/order-success";
        // Открывает созданный шаблон order-success.html
        // After order was successfully created, open the confirmation
    }

}
