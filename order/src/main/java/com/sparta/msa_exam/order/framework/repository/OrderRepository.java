package com.sparta.msa_exam.order.framework.repository;

import com.sparta.msa_exam.order.domain.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

}
