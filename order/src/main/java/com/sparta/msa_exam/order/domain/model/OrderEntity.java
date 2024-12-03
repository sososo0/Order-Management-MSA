package com.sparta.msa_exam.order.domain.model;

import com.sparta.common.BaseEntity;
import com.sparta.msa_exam.order.application.domain.Order;
import com.sparta.msa_exam.order.application.domain.OrderProduct;
import com.sparta.msa_exam.order.domain.model.vo.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_order")
public class OrderEntity extends BaseEntity {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProductEntity> productIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255)")
    private OrderStatus orderStatus;

    @Column(name = "order_total_price", nullable = false)
    private int orderTotalPrice;

    // TODO: UserEntity 추가하기

    public void setProductIds(
        OrderProductEntity product
    ) {
        this.productIds.add(product);
        product.setOrder(this);
    }

    public void updateProductIds(
        OrderProductEntity product,
        Long userId
    ) {
        super.updateFrom(userId);
        this.productIds.add(product);
        product.updateOrder(this, userId);
    }

    public void updateOrderStatus(
        OrderStatus orderStatus
    ) {
        this.orderStatus = orderStatus;
    }

    private OrderEntity(
        OrderStatus orderStatus,
        int orderTotalPrice,
        Long userId
    ) {
        super(userId, userId);
        this.userId = userId;
        this.orderStatus = orderStatus;
        this.orderTotalPrice = orderTotalPrice;
    }

    public static OrderEntity toEntity(
        OrderStatus orderStatus,
        int orderTotalPrice,
        Long userId
    ) {
        return new OrderEntity(
            orderStatus,
            orderTotalPrice,
            userId
        );
    }

    public Order toDomain() {
        List<OrderProduct> products = productIds.stream()
            .map(orderProduct -> new OrderProduct(
                orderProduct.getId(),
                orderProduct.getProductId(),
                orderProduct.getQuantity())
            )
            .collect(Collectors.toList());

        return new Order(
            this.id,
            this.userId,
            this.orderStatus,
            products,
            this.orderTotalPrice,
            super.getCreatedAt()
        );
    }
}
