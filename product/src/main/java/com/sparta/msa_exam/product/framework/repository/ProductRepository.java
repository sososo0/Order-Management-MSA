package com.sparta.msa_exam.product.framework.repository;

import com.sparta.msa_exam.product.domain.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

}
