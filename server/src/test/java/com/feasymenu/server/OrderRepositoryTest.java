package com.feasymenu.server;

import com.feasymenu.server.model.OrderEntity;
import com.feasymenu.server.model.OrderStatus;
import com.feasymenu.server.model.TableEntity;
import com.feasymenu.server.repository.OrderRepository;
import com.feasymenu.server.repository.TableRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    OrderRepository repository;

    @Autowired
    TableRepository tableRepository;

    @Test
    void saveAndFind() {
        TableEntity t = tableRepository.save(TableEntity.builder().code("T1").build());
        OrderEntity o = new OrderEntity();
        o.setCustomerName("Test");
        o.setTable(t);
        o.setStatus(OrderStatus.NEW);
        o.setTotal(BigDecimal.valueOf(10));
        OrderEntity saved = repository.save(o);
        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findByTable_Code("T1")).isNotEmpty();
    }
}
