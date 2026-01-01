package com.pocketmon.insightpocket.domain.laneige.entity;

import com.pocketmon.insightpocket.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(
        name = "laneige_products",
        indexes = @Index(name = "ux_laneige_products_url", columnList = "product_url", unique = true)
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class LaneigeProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_url", length = 2000, nullable = false)
    private String productUrl;

    @Column(name = "product_name", length = 1000, nullable = false)
    private String productName;

    @Column(name = "image_url", length = 2000)
    private String imageUrl;

    @Column(name = "style", length = 300)
    private String style;

    @Lob
    @Column(name = "customers_say_current")
    private String customersSayCurrent;

    @Column(name = "customers_say_hash", length = 64)
    private String customersSayHash;

    @Column(name = "customers_say_updated_at")
    private java.time.LocalDateTime customersSayUpdatedAt;

    public static LaneigeProduct create(
            String productUrl,
            String productName,
            String imageUrl,
            String style,
            String customersSay,
            String customersSayHash
    ) {
        LaneigeProduct p = new LaneigeProduct();
        p.productUrl = productUrl;
        p.productName = productName;
        p.imageUrl = imageUrl;
        p.style = style;
        p.customersSayCurrent = customersSay;
        p.customersSayHash = customersSayHash;
        p.customersSayUpdatedAt = LocalDateTime.now();
        return p;
    }

    public void updateBasicInfo(
            String productName,
            String imageUrl,
            String style
    ) {
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.style = style;
    }

    public void updateCustomersSayIfChanged(
            String newCustomersSay,
            String newHash
    ) {
        if (newHash == null) return;

        if (this.customersSayHash == null || !this.customersSayHash.equals(newHash)) {
            this.customersSayCurrent = newCustomersSay;
            this.customersSayHash = newHash;
            this.customersSayUpdatedAt = LocalDateTime.now();
        }
    }
}