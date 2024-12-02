package com.sparta.msa_exam.order.application.domain;

import com.sparta.msa_exam.order.domain.model.vo.OrderStatus;
import java.util.List;

public record OrderForCreate(
//    String userId TODO: user 추가하기,
    List<OrderProductForCreate> products,
    OrderStatus status
) {

}
