package shop.hooking.hooking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.hooking.hooking.config.jwt.JwtTokenProvider;
import shop.hooking.hooking.dto.request.MemberFormDto;
import shop.hooking.hooking.dto.response.LoginInfoDto;
import shop.hooking.hooking.entity.Member;
import shop.hooking.hooking.exception.PasswordNotMatchedException;
import shop.hooking.hooking.exception.UserNotFoundException;
import shop.hooking.hooking.repository.MemberRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    @Transactional
    public Member signup(MemberFormDto memberFormDto) {
        memberFormDto.setPassword(passwordEncoder.encode(memberFormDto.getPassword()));
        Member member = memberRepository.save(memberFormDto.toEntity());
        /* Profile profile = Profile.builder()
                .file(null)
                .bio("반갑습니다 :)")
                .build();
        profileRepository.save(profile);*/
        return member;
    }

    /*
    private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }
    */


    public LoginInfoDto login(String email, String password) {
        Member member = memberRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        checkPassword(password, member.getPassword());
        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail(), member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail(), member.getRole());
        return new LoginInfoDto(accessToken, refreshToken, member.getNickname());
    }



    //전달받은 유저의 이메일로 유저가 존재하는지 확인 + refreshtokne이 유효한지 체크
    //accessToken 재생성하여 refreshToken과 함께 응답
    public LoginInfoDto reIssueAccessToken(String email, String refreshToken) {
        Member member = memberRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        jwtTokenProvider.checkRefreshToken(email, refreshToken);
        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail(), member.getRole());
        return new LoginInfoDto(accessToken, refreshToken, member.getNickname());
    }


    //쿠키에 토큰을 담아 프론트로 전송
    public ResponseCookie generateCookie(String type, String token)
    {
        ResponseCookie cookie = ResponseCookie.from(type, token)
                .maxAge(7 * 24 * 60 * 60)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        return cookie;

    }

    //DB에 있는 비밀번호와 사용자로부터 받은 비밀번호의 일치여부 확인
    private void checkPassword(String password, String encodedPassword) {
        boolean isSame = passwordEncoder.matches(password, encodedPassword);
        if (!isSame) {
            throw new PasswordNotMatchedException();
        }

    }

    public LoginInfoDto provideToken(String email)
    {
        Member member = memberRepository
                .findByEmail(email).orElseThrow(UserNotFoundException::new);
        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail(), member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail(), member.getRole());
        return new LoginInfoDto(accessToken, refreshToken, member.getNickname());
    }


    public void logout(String email, String accessToken) {
        jwtTokenProvider.logout(email, accessToken);
    }

}