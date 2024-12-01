package com.sparta.msa_exam.product.framework.repository;

import com.sparta.msa_exam.product.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
