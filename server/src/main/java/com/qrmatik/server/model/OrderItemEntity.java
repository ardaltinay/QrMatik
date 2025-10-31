package com.qrmatik.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemEntity extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id")
    @JsonIgnore
    private MenuItemEntity menuItem;

    private Integer quantity;

    private BigDecimal unitPrice;

    // Snapshot fields for historical integrity
    private String nameSnapshot;
    private String imageSnapshot;
    private String categorySnapshot;
    private String subcategorySnapshot;

    // Optional customer note per line
    @Column(name = "note", length = 1000)
    private String note;

    // tenant info derived via order.tenant; no separate tenantCode field
}
