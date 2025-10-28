package com.qrmatik.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.qrmatik.server.converter.OrderStatusConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity extends AbstractEntity {
    private String sessionId; // simple session token for customer

    private String customerName;

    @Convert(converter = OrderStatusConverter.class)
    private OrderStatus status; // NEW, PREPARING, READY, SERVED, PAYMENT_COMPLETED

    private BigDecimal total;

    private LocalDateTime sessionExpiresAt;

    // İlişkisel sipariş kalemleri (geri uyumluluk için response'ta saklıyoruz)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<OrderItemEntity> lines = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private TenantEntity tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private TableEntity table;

}
