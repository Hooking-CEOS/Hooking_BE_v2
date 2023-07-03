package shop.hooking.hooking.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "review")
@Where(clause = "delete_yn = 0")
public class Review extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; //건의사항 제목

    private String content; //건의사항 내용

    private Long writerId; //작성자

    @Column(name = "created_at")
    private LocalDateTime createdTime;

    @Column(name = "delete_yn")
    private boolean deleteYn; // 삭제여부

    @PrePersist
    public void prePersist() {
        createdTime = LocalDateTime.now();
    }

    @Builder
    public Review(String title, String content, Long writeId,LocalDateTime createdTime){
        this.title = title;
        this.content = content;
        this.writerId = writeId;
        this.createdTime = createdTime;
    }

}
