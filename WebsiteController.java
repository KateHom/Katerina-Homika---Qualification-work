package com.example.qualificationwork.controllers;

import com.example.qualificationwork.models.Cart;
import com.example.qualificationwork.models.Product;
import com.example.qualificationwork.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;

@Controller
@SessionAttributes("cart")
public class WebsiteController {

    private final ProductRepository productRepository;

    public WebsiteController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/")

    public String home(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        model.addAttribute("currency", "€");
        model.addAttribute("store", "Cosmetics Store - Home"); // store название странички и главное значение зто Главная страница
        return "website/home";
        // вызываем определенный html шаблон, который в зтом случае называется home
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("store", "Cosmetics Store - About us"); // store название странички и главное значение зто О нас
        return "website/about";
        // вызываем определенный html шаблон, который в этом случае называется about
    }

    @GetMapping("/support")
    public String support(Model model) {
        model.addAttribute("store", "Cosmetics Store - Support"); // store название странички и главное значение зто Поддержка
        return "website/support";
        // вызываем определенный html шаблон, который в зтом случае называется support
    }

    // Глобальный атрибут для подсчета количества товаров в корзине (доступен во всех шаблонах)
    // Global attribute to count items in the cart (available across all templates)
    @ModelAttribute("cartCount")
    public int getCartCount(@ModelAttribute("cart") Cart cart) {
        return cart.getItems().values().stream().mapToInt(Integer::intValue).sum();
    }

}