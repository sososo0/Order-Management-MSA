package com.sparta.msa_exam.product.domain.model;

import com.sparta.common.BaseEntity;
import com.sparta.msa_exam.product.application.domain.Product;
import com.sparta.msa_exam.product.application.domain.ProductForCreate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_product")
public class ProductEntity extends BaseEntity {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name = "name", columnDefinition = "varchar(255)")
    private String name;

    @Column(nullable = false, name = "supply_price")
    private Integer supplyPrice;

    private ProductEntity(
        String name,
        Integer supplyPrice,
        Long userId
    ) {
        super(userId, userId);
        this.name = name;
        this.supplyPrice = supplyPrice;
    }

    public static ProductEntity toEntity(ProductForCreate productForCreate) {
        return new ProductEntity(
            productForCreate.name(),
            productForCreate.supplyPrice(),
            1L
        );
    }

    public Product toDomain() {
        return new Product(
            this.id,
            this.name,
            this.supplyPrice
        );
    }
}
