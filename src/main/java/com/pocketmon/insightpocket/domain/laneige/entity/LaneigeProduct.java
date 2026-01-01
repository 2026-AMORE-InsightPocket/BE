package com.pocketmon.insightpocket.domain.laneige.entity;

import com.pocketmon.insightpocket.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}