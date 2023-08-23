package shop.hooking.hooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.entity.Member;
import shop.hooking.hooking.repository.MemberRepository;


//토큰에 세팅된 유저정보로 회원정보를 조회
@Service
@RequiredArgsConstructor
public class CustomUsersDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // memberRepository를 통해 memberemail에 해당하는 member의 존재여부만 판단
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));
        System.out.println(member.getEmail());
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }
}
