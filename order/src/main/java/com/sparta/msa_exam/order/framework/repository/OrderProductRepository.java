package com.sparta.msa_exam.order.framework.repository;

import com.sparta.msa_exam.order.domain.model.OrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProductEntity, Long> {

}
