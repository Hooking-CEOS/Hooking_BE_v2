package shop.hooking.hooking.entity;


import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;


import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
@Setter
@Entity
@Where(clause = "delete_flag=0")
@Table(name = "draft")
public class Draft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="draft_id")
    private Long draftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id")
    private Category category;

    @Column(name="created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(name="delete_flag")
    @NotNull
    private Boolean deleteFlag;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public Draft(User user,Category category , LocalDateTime createdAt,String text) {
        this.user=user;
        this.category=category;
        this.createdAt=createdAt;
        this.text = text;
        this.deleteFlag = false;
    }
}
