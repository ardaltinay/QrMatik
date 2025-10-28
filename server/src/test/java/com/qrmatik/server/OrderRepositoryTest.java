package com.qrmatik.server;

import static org.assertj.core.api.Assertions.assertThat;

import com.qrmatik.server.model.OrderEntity;
import com.qrmatik.server.model.OrderStatus;
import com.qrmatik.server.model.TableEntity;
import com.qrmatik.server.repository.OrderRepository;
import com.qrmatik.server.repository.TableRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
