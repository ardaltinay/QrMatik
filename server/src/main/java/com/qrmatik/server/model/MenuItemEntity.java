package com.qrmatik.server.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemEntity extends AbstractEntity {
    private String name;

    private BigDecimal price;

    private String category;

    // Alt kategori (ör. pizza, salad, soda)
    @Column(name = "sub")
    private String subcategory;

    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private TenantEntity tenant;
}
