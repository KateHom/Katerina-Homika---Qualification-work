package com.example.qualificationwork.repository;

import com.example.qualificationwork.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
