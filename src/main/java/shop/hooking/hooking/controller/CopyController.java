package shop.hooking.hooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.hooking.hooking.dto.request.CopyReq;
import shop.hooking.hooking.entity.Card;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.repository.CardRepository;
import shop.hooking.hooking.service.CopyService;
import shop.hooking.hooking.service.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/copy")
public class CopyController {

    private final JwtTokenProvider jwtTokenProvider;
    private final CopyService copyService;
    private final CardRepository cardRepository;


    //전체 카피라이팅 조회
    @GetMapping("")
    ResponseEntity<?> copyList(){
        return new ResponseEntity<>(copyService.getCopyList(), HttpStatus.OK);
    }

    //카피라이팅 검색 조회
//    @GetMapping("/search")
//    ResponseEntity<?> copySearchList(@RequestParam(name = "keyword") String q){
//        return new ResponseEntity<>(copyService.selectCopyByQuery(q), HttpStatus.OK);
//    }

    //스크랩한 카피라이팅 조회(유저가 스크랩한 거 가져와)
    @GetMapping("/scrap")
    ResponseEntity<?> copyScrapList(HttpServletRequest httpRequest){
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        return new ResponseEntity<>(copyService.getCopyScrapList(user), HttpStatus.OK);
    }

    //카피라이팅 스크랩
    @CrossOrigin(origins = "https://hooking.shop, https://hooking-dev.netlify.app/, https://hooking.netlify.app/, http://localhost:3000, http://localhost:3001")
    @PostMapping("/scrap")
    ResponseEntity<?> copyScrap(HttpServletRequest httpRequest, @RequestBody CopyReq copyReq) throws IOException {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        Card card = cardRepository.findCardById(copyReq.getCardId());
        copyService.saveCopy(user, card);

        return new ResponseEntity<>("스크랩 완료",HttpStatus.OK);

    }

    //카피라이팅 필터
//    @PostMapping("/filter")
//    ResponseEntity<?> copyFilter(HttpServletRequest httpRequest, @RequestBody CopyReq copyReq) throws IOException {
//        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
//
//        return new ResponseEntity<>(copyService.saveCopy(user.getId(), copyReq.getBrandId()), HttpStatus.OK);
//
//    }

}
