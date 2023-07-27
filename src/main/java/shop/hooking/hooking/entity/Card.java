package shop.hooking.hooking.entity;

import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.*;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
@Entity
@DynamicInsert
@Table(name="card")
@OnDelete(action = OnDeleteAction.CASCADE)
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(name = "url")
    private String url;


    @NotNull
    @Column(name = "scrap_cnt")
    @ColumnDefault("0")
    private Integer scrapCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="brand_id")
    private Brand brand;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Scrap> scraps = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.scrapCnt= this.scrapCnt == null ? 0 : this.scrapCnt;
    }

    @Builder
    public Card(String text, Integer scrapCnt, Brand brand, LocalDateTime createdAt,String url) {
        this.text = text;
        this.scrapCnt = (scrapCnt!=null)?scrapCnt:0;
        this.brand = brand;
        this.createdAt = createdAt;
        this.url=url;
    }

}
