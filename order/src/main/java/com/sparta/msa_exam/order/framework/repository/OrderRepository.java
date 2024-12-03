package com.sparta.msa_exam.order.framework.repository;

import com.sparta.msa_exam.order.domain.model.OrderEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

}
