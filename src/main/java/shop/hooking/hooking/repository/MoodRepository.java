package shop.hooking.hooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.hooking.hooking.entity.Mood;

import java.util.List;

public interface MoodRepository extends JpaRepository<Mood, Long> {

    Mood findMoodById(Long id);
}
