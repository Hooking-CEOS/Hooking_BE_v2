package shop.hooking.hooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.dto.request.CategoryReqDto;
import shop.hooking.hooking.dto.request.DraftReqDto;
import shop.hooking.hooking.dto.response.*;
import shop.hooking.hooking.entity.*;
import shop.hooking.hooking.exception.CardNotFoundException;
import shop.hooking.hooking.exception.OutOfIndexException;
import shop.hooking.hooking.global.jwt.JwtTokenProvider;
import shop.hooking.hooking.repository.CategoryRepository;
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

    private final CategoryRepository categoryRepository;

    // 카테고리 없을 때
    @Transactional
    public void createCategory(HttpServletRequest httpRequest, CategoryReqDto categoryReqDto) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);

        // 이미 같은 이름의 카테고리가 있는지 확인
        Optional<Category> existingCategory = categoryRepository.findByCategoryName(categoryReqDto.getCategoryName());

        // 이미 존재하는 경우, 예외처리 또는 다른 로직 수행
        if (existingCategory.isPresent()) {
            throw new RuntimeException("Category with the same name already exists");
        }

        // 존재하지 않는 경우 새로운 카테고리 생성
        Category newCategory = Category.builder()
                .user(user)
                .categoryName(categoryReqDto.getCategoryName())
                .createdAt(categoryReqDto.getCreatedAt())
                .build();


        // 저장
        categoryRepository.save(newCategory);
    }


    // 카테고리 있을 때 Draft 생성
    @Transactional
    public void createDraft(HttpServletRequest httpRequest, DraftReqDto draftReqDto){
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);

        Category category = categoryRepository.findByCategoryName(draftReqDto.getCategoryName())
                .orElseGet(() -> Category.builder()
                        .categoryName(draftReqDto.getCategoryName())
                        .build());

        Draft draft = Draft.builder()
                .user(user)
                .category(category)
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
                .categoryName(draft.getCategory().getCategoryName())
                .build();
    }

    public CategoryResDto getDraftList(HttpServletRequest httpRequest, int index) {
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

        // 3개씩 묶어 반환
        List<CategoryDraftResDto> result = paginate(categoryDraftResList, index);

        // 최종적으로 CategoryResDto를 생성하고 반환
        return CategoryResDto.builder()
                .data(result)
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

        // 3개씩 묶어 반환
        List<CategoryDraftResDto> result = paginate(categoryDraftResList, index);

        // 결과를 CategoryResDto로 묶어서 반환
        return CategoryResDto.builder()
                .data(result)
                .build();
    }


    // 인덱싱을 수행하는 함수
    private List<CategoryDraftResDto> paginate(List<CategoryDraftResDto> list, int index) {
        int startIndex = index * 3;
        int endIndex = Math.min(startIndex + 3, list.size());
        if (startIndex >= endIndex) {
            throw new OutOfIndexException();
        }
        return list.subList(startIndex, endIndex);
    }

    public void deleteDraft(Long draftId){
        Draft draft = draftRepository.findBydraftId(draftId);
        draft.setDeleteFlag(true);
        draftRepository.save(draft);
    }

}
