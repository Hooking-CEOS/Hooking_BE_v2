package shop.hooking.hooking.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="category_name", columnDefinition = "TEXT")
    private String categoryName;

    @Column(name="created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public Category(User user, String categoryName, LocalDateTime createdAt) {
        this.user=user;
        this. categoryName=categoryName;
        this.createdAt=createdAt;
    }
}

