package com.qrmatik.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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

    // Stok kontrolü (yalnızca PRO plan özellikleri UI/API üzerinden açılır)
    // stockEnabled = true ise stockQuantity değeri anlamlıdır; false ise sınırsız
    // kabul edilir.
    @Column(name = "stock_enabled")
    private Boolean stockEnabled; // null => varsayılan false (sınırsız)

    @Column(name = "stock_qty")
    private Integer stockQuantity; // null => sınırsız
}
