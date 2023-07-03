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
@Table(name="mood")
public class Mood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    @Column(name="mood_name")
    private String moodName;

    @Builder
    public Mood(String moodName) {
        this.moodName = moodName;
    }
}
