package com.sparta.msa_exam.product.framework.repository;

import com.sparta.msa_exam.product.domain.model.ProductEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query("select p from ProductEntity p where p.id = :productId")
    Optional<ProductEntity> findByProductId(Long productId);
}
