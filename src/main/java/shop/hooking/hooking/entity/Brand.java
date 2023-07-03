package shop.hooking.hooking.entity;


import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.hooking.hooking.config.Role;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="brand")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    @Column(name="brand_name")
    private String brandName; // 브랜드 이름

    @NotNull
    @Column(name="brand_intro")
    private String brandIntro; // 브랜드 한줄소개

    @NotNull
    @Column(name="brand_link")
    private String brandLink; // 브랜드 인스타 링크

    @NotNull
    @Column(name="brand_product")
    private String brandProduct; // 브랜드 타켓 제품

    @NotNull
    @Column(name="brand_age")
    private String brandAge; // 브랜드 나이대

    @NotNull
    @Column(name="brand_price")
    private String brandPrice; // 브랜드 가격대

    @Builder
    public Brand(String brandName,String brandIntro,String brandLink,String brandProduct,String brandAge,String brandPrice) {
        this.brandName = brandName;
        this.brandIntro = brandIntro;
        this.brandLink = brandLink;
        this.brandProduct = brandProduct;
        this.brandAge = brandAge;
        this.brandPrice = brandPrice;
    }
}
