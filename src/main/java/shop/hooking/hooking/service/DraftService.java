package shop.hooking.hooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.dto.request.DraftReqDto;
import shop.hooking.hooking.dto.response.*;
import shop.hooking.hooking.entity.Card;
import shop.hooking.hooking.entity.Draft;
import shop.hooking.hooking.entity.Scrap;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.exception.CardNotFoundException;
import shop.hooking.hooking.global.jwt.JwtTokenProvider;
import shop.hooking.hooking.repository.DraftJpaRepository;
import shop.hooking.hooking.repository.DraftRepository;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DraftService {

    private final JwtTokenProvider jwtTokenProvider;
    private final DraftRepository draftRepository;
    private final DraftJpaRepository draftJpaRepository;

    @Transactional
    public void createDraft(HttpServletRequest httpRequest, DraftReqDto draftReqDto){
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        Draft draft = Draft.builder()
                .user(user)
                .categoryName(draftReqDto.getCategoryName())
                .createdAt(draftReqDto.getCreatedAt())
                .text(draftReqDto.getText())
                .build();

        draftRepository.save(draft);
    }

    @Transactional
    public DraftResDto createDraftRes(Draft draft) {

        return DraftResDto.builder()
                .text(draft.getText())
                .createdAt(draft.getCreatedAt())
                .categoryName(draft.getCategoryName())
                .build();
    }
    public CategoryResDto getDraftList(HttpServletRequest httpRequest,int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);

        //  현재 사용자의 모든 Draft을 데이터베이스에서 조회
        List<Draft> drafts = draftRepository.findByUser(user);

        // Draft을 categoryName으로 그룹화하여 Map에 저장
        Map<String, List<DraftResDto>> draftsByCategory = drafts.stream()
                .map(this::createDraftRes) // 각 Draft을 DraftResDto로 변환
                .collect(Collectors.groupingBy(DraftResDto::getCategoryName)); // categoryName으로 그룹화

        // 각 카테고리별로 CategoryDraftResDto를 생성하고 리스트에 추가
        List<CategoryDraftResDto> categoryDraftResList = draftsByCategory.entrySet().stream()
                .map(entry -> CategoryDraftResDto.builder()
                        .drafts(entry.getValue())
                        .categoryName(entry.getKey())
                        .totalNum(countDraft(entry.getValue()))
                        .build())
                .collect(Collectors.toList());

        // 최종적으로 CategoryResDto를 생성하고 반환
        return CategoryResDto.builder()
                .data(categoryDraftResList)
                .build();
    }

    @Transactional
    public int countDraft(List<DraftResDto> draftResDtos) {
        return draftResDtos.size();
    }

    public CategoryResDto searchDraftList(HttpServletRequest httpRequest, String q, int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);

        // DraftJpaRepository를 사용하여 text 검색
        List<Draft> drafts = draftJpaRepository.searchUserDraft(user,q);

        // 각 Draft을 categoryName으로 그룹화하여 Map에 저장
        Map<String, List<DraftResDto>> draftsByCategory = drafts.stream()
                .map(this::createDraftRes) // 각 Draft을 DraftResDto로 변환
                .collect(Collectors.groupingBy(DraftResDto::getCategoryName)); // categoryName으로 그룹화

        // 각 카테고리별로 CategoryDraftResDto를 생성하고 리스트에 추가
        List<CategoryDraftResDto> categoryDraftResList = draftsByCategory.entrySet().stream()
                .map(entry -> CategoryDraftResDto.builder()
                        .drafts(entry.getValue())
                        .categoryName(entry.getKey())
                        .totalNum(countDraft(entry.getValue()))
                        .build())
                .collect(Collectors.toList());

        // 결과를 CategoryResDto로 묶어서 반환
        return CategoryResDto.builder()
                .data(categoryDraftResList)
                .build();
    }



}
