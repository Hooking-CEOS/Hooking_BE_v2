package shop.hooking.hooking.entity;


import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.hooking.hooking.config.Role;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
@Table(name="brand")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    @Column(name="brand_name")
    private String brandName;

    @NotNull
    @Column(name="brand_intro")
    private String brandIntro;

    @NotNull
    @Column(name="brand_link")
    private String brandLink;

    @NotNull
    @Column(name="brand_product")
    private String brandProduct;

    @NotNull
    @Column(name="brand_age")
    private String brandAge;

    @NotNull
    @Column(name="brand_price")
    private String brandPrice;

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
