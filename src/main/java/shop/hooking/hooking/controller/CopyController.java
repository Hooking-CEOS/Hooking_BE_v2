package shop.hooking.hooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.hooking.hooking.dto.CardSearchCondition;
import shop.hooking.hooking.dto.HttpRes;
import shop.hooking.hooking.dto.request.CopyReq;
import shop.hooking.hooking.dto.response.CopyRes;
import shop.hooking.hooking.entity.Card;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.exception.BadRequestException;
import shop.hooking.hooking.repository.CardJpaRepository;
import shop.hooking.hooking.repository.CardRepository;
import shop.hooking.hooking.service.BrandService;
import shop.hooking.hooking.service.CopyService;
import shop.hooking.hooking.service.JwtTokenProvider;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/copy")
public class CopyController {

    private final JwtTokenProvider jwtTokenProvider;

    private final CopyService copyService;

    private final CardRepository cardRepository;

    private final CardJpaRepository cardJpaRepository;


    // 전체 카피라이팅 조회
    @GetMapping("")
    public List<CopyRes> copyList(){
        List<CopyRes> copyRes = copyService.getCopyList();
        return copyRes;
    }


    // 카피라이팅 검색 조회
    @GetMapping("/search")
    public List<CopyRes> copySearchList(@RequestParam(name = "keyword") String q){
        if(q.isEmpty()){
            throw new BadRequestException("검색 결과를 찾을 수 없습니다.");
        }

        if(q.equals("프레시안") || q.equals("롬앤") || q.equals("헤라") || q.equals("피지오겔")
                || q.equals("멜릭서") || q.equals("려") || q.equals("이니스프리") || q.equals("설화수")
                || q.equals("에뛰드") || q.equals("미샤")|| q.equals("아비브") || q.equals("에스트라")
                || q.equals("베네피트") || q.equals("숨37도") || q.equals("오휘") || q.equals("fmgt")
                || q.equals("네이밍") || q.equals("키스미") || q.equals("힌스") || q.equals("데이지크")
                || q.equals("애프터 블로우") || q.equals("더바디샵") || q.equals("롱테이크") || q.equals("어뮤즈")
                || q.equals("탬버린즈") || q.equals("논픽션") || q.equals("에스쁘아") || q.equals("스킨푸드")){

            List<CopyRes> copyRes = copyService.selectBrandByQuery(q);

            return copyRes;

        } else if (q.equals("퓨어한") || q.equals("화려한") || q.equals("키치한") || q.equals("고급스러운")
                || q.equals("자연의") || q.equals("심플한") || q.equals("네추럴한") || q.equals("발랄한")
                || q.equals("독특한") || q.equals("비비드한") || q.equals("첨단의") || q.equals("도시적인")
                || q.equals("감각적인") || q.equals("수줍은") || q.equals("전통적인") || q.equals("친근한")) {

            List<CopyRes> copyRes = copyService.selectMoodByQuery(q);

            return copyRes;

        } else{

            List<CopyRes> copyRes = copyService.selectCopyByQuery(q);

            return copyRes;

        }

    }


    // 스크랩한 카피라이팅 조회
    @GetMapping("/scrap")
    public List<CopyRes> copyScrapList(HttpServletRequest httpRequest){
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        List<CopyRes> copyRes = copyService.getCopyScrapList(user);
        return copyRes;
    }


    // 카피라이팅 스크랩
    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000, http://localhost:3001")
    @PostMapping("/scrap")
    public HttpRes<String> copyScrap(HttpServletRequest httpRequest, @RequestBody CopyReq copyReq) throws IOException {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        Card card = cardRepository.findCardById(copyReq.getCardId());
        boolean isScrap = copyService.saveCopy(user, card); // 스크랩됐으면->true, 안됐으면->false
        if(isScrap){
            return new HttpRes<>("스크랩을 완료하였습니다.");
        }

        return new HttpRes<>("스크랩에 실패하였습니다.");
    }


    //카피라이팅 필터
    @GetMapping("/filter")
    public List<CopyRes> searchFilterCard(CardSearchCondition condition) {
        List<CopyRes> results = cardJpaRepository.search(condition);
        Collections.shuffle(results);
        return results;
    }
}
