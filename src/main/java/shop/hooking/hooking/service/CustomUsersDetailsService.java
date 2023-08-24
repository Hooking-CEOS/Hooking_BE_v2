//package shop.hooking.hooking.service;
//
//import lombok.RequiredArgsConstructor;
////import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import shop.hooking.hooking.repository.UserRepository;
//import shop.hooking.hooking.entity.User;
//
//import java.util.Collection;
//
//
////토큰에 세팅된 유저정보로 회원정보를 조회
//@Service
//@RequiredArgsConstructor
//public class CustomUsersDetailsService implements UserDetailsService{
//
//    private final UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(Long Id) throws UsernameNotFoundException {
//        // memberRepository를 통해 memberemail에 해당하는 member의 존재여부만 판단
//        User user = userRepository.findUserById(Id);
//
//        UserDetails customUserDetails = new User(
//                user.getEmail(),
//                user.getPassword(),
//                user.getRole().toString()
//        )
//
//        return customUserDetails;
//    }
//
//}
//
