package shop.hooking.hooking.entity;

import lombok.*;
import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "contain")
public class Contain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contain_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="foler_id")
    private Folder folder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="scrap_id")
    private Scrap scrap;



    @Builder
    public Contain(Folder folder, Scrap scrap) {
        this.folder=folder;
        this.scrap=scrap;
    }
}
