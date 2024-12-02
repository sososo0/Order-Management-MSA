package com.sparta.msa_exam.order.domain.model;

import com.sparta.common.BaseEntity;
import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.application.domain.OrderProductForCreate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_order_product")
public class OrderProductEntity extends BaseEntity {

    @Id
    @Column(name = "order_product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    // TODO: user 추가

    public void setOrder(OrderEntity orderEntity) {
        this.order = orderEntity;
    }

    public void updateOrder(OrderEntity order, Long userId) {
        super.updateFrom(userId);
        this.order = order;
    }

    private OrderProductEntity(
        OrderEntity orderEntity,
        Long productId,
        int quantity,
        Long userId
    ) {
        super(userId, userId);
        this.order = orderEntity;
        this.productId = productId;
        this.quantity = quantity;
    }

    public void updateQuantity(int quantity) {
        this.quantity += quantity;
    }

    public static OrderProductEntity toEntity(
        OrderEntity orderEntity,
        Long productId,
        int quantity
    ) {
        return new OrderProductEntity(
            orderEntity,
            productId,
            quantity,
            1L
        );
    }
}
