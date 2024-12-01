package com.sparta.msa_exam.product.domain.model;

import com.sparta.common.BaseEntity;
import com.sparta.msa_exam.product.framework.web.dto.ProductCreateInputDTO;
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
public class Product extends BaseEntity {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name = "name", columnDefinition = "varchar(255)")
    private String name;

    @Column(nullable = false, name = "supply_price")
    private Integer supplyPrice;

    private Product(
        String name,
        Integer supplyPrice,
        Long userId
    ) {
        super(userId, userId);
        this.name = name;
        this.supplyPrice = supplyPrice;
    }

    public static Product toEntity(ProductCreateInputDTO createInputDTO) {
        return new Product(
            createInputDTO.name(),
            createInputDTO.supplyPrice(),
            1L
        );
    }
}
