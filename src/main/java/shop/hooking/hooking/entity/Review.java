package shop.hooking.hooking.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "review")
@Where(clause = "delete_yn = 0")
public class Review { //베이스타임 엔티티상속 받아야함

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content; //건의사항 내용

    private Long writerId; //작성자

    @Column(name = "delete_yn")
    private boolean deleteYn; // 삭제여부

}
