//package shop.hooking.hooking.repository;
//
//
//import io.lettuce.core.dynamic.annotation.Param;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import shop.hooking.hooking.entity.Member;
//
//import java.util.Optional;
//
//
//public interface MemberRepository extends JpaRepository<Member, Long>{
//    //Member findByEmail (String email); //회원 가입 시 중복된 회원이 있는지 검사하기 위해서 이메일로 검사
//
//
//    boolean existsByEmail(String email);
//
//
//    Optional<Member> findByEmail(@Param("email") String email);
//}