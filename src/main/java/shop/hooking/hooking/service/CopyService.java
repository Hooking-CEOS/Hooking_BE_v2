package shop.hooking.hooking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.config.enumtype.BrandType;
import shop.hooking.hooking.config.enumtype.MoodType;
import shop.hooking.hooking.dto.request.ScrapReqDto;
import shop.hooking.hooking.dto.response.CopyResDto;
import shop.hooking.hooking.global.jwt.JwtTokenProvider;
import shop.hooking.hooking.dto.CardSearchCondition;
import shop.hooking.hooking.dto.request.CopyReqDto;
import shop.hooking.hooking.dto.request.CrawlingData;
import shop.hooking.hooking.dto.request.CrawlingReqDto;
import shop.hooking.hooking.dto.response.CopySearchResDto;
import shop.hooking.hooking.entity.*;
import shop.hooking.hooking.exception.*;
import shop.hooking.hooking.repository.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;


@Service
@RequiredArgsConstructor
public class CopyService {

    private final CardRepository cardRepository;
    private final BrandRepository brandRepository;
    private final ScrapRepository scrapRepository;
    private final CardJpaRepository cardJpaRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final FolderRepository folderRepository;
    private final ContainRepository containRepository;

    //상속 -> 일반 부모 , 카카오 자식
    public List<CopyResDto> getCopyList(HttpServletRequest httpRequest, int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        Long[] brandIds = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L};
        List<CopyResDto> tempCopyRes = new ArrayList<>();
        for (Long brandId : brandIds) {
            List<CopyResDto> copyRes = getTopEightCopy(brandId);
            tempCopyRes.addAll(copyRes);
        }
        Collections.shuffle(tempCopyRes);
        List<CopyResDto> resultCopyRes = getCopyByIndex(tempCopyRes, index);
        setScrapCnt(httpRequest, resultCopyRes);
        setIsScrap(user, resultCopyRes);
        return resultCopyRes;

    }



    public List<CopyResDto> getCopyByIndex(List<CopyResDto> copyResList, int index) {
        int startIndex = index * 30;
        int endIndex = Math.min(startIndex + 30, copyResList.size());
        if (startIndex >= endIndex) {
            throw new OutOfIndexException();
        }
        return copyResList.subList(startIndex, endIndex);
    }



    public void setScrapCnt(HttpServletRequest httpRequest, List<CopyResDto> copyResList) {
        String token = httpRequest.getHeader("Authorization");
        if (token == null) {
            for (CopyResDto copyRes : copyResList) {
                copyRes.setScrapCnt(0);
            }
        }
    }

    public void setIsScrap(User user, List<CopyResDto> copyResList) {
        List<Scrap> scraps = scrapRepository.findScrapByUser(user);

        for (CopyResDto copyRes : copyResList) {
            long cardId = copyRes.getId();
            boolean isScrapFound = scraps.stream().anyMatch(scrap -> scrap.getCard().getId() == cardId);
            copyRes.setIsScrap(isScrapFound ? 1 : 0);
        }
    }

    @Transactional
    public List<CopyResDto> getTopEightCopy(Long brandId) {
        List<Card> cards = cardRepository.findTop8ByBrandIdOrderByCreatedAtDesc(brandId);
        List<CopyResDto> copyResList = new ArrayList<>();

        for (Card card : cards) {
            CopyResDto copyRes = createCopyRes(card);
            copyResList.add(copyRes);
        }

        return copyResList;
    }

    public CopySearchResDto searchBrandList(HttpServletRequest httpRequest, String q, int index, Long randomSeedDto) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);

        q = checkKeyword(q);

        List<CopyResDto> brandCopyRes;

        if (BrandType.containsKeyword(q)) {
            // index가 0이면 백엔드에서 랜덤 시드값 생성
            if (index == 0) {
                long seed = new Random().nextInt(1000001); // 랜덤 시드값 생성
                brandCopyRes = cardJpaRepository.searchBrand(q);
                setScrapCnt(httpRequest, brandCopyRes);
                setIsScrap(user, brandCopyRes);
                Collections.shuffle(brandCopyRes, new Random(seed)); // 생성한 시드값으로 섞기

                // 생성한 랜덤 시드값과 함께 결과 반환
                CopySearchResDto brandSearchResult = createCopySearchResult("brand", q, brandCopyRes, index, seed);
                return brandSearchResult;
            } else if (index > 0) {
                // index가 1 이상이면 프론트엔드에서 전달한 시드값 사용
                // 프론트엔드에서 전달받은 시드값으로 섞기
                // 여기서는 seed를 프론트엔드에서 전달받는다고 가정합니다.
                Long seed = randomSeedDto; // 프론트엔드에서 전달받은 시드값
                brandCopyRes = cardJpaRepository.searchBrand(q);
                setScrapCnt(httpRequest, brandCopyRes);
                setIsScrap(user, brandCopyRes);
                Collections.shuffle(brandCopyRes, new Random(seed)); // 전달받은 시드값으로 섞기

                CopySearchResDto brandSearchResult = createCopySearchResult("brand", q,brandCopyRes, index, seed);
                return brandSearchResult;
            }
        }

        // 검색 결과가 없다면
        throw new CardNotFoundException();
    }



    public String checkKeyword(String q) {
        if (q.equals("애프터블로우")) {
            q = "애프터 블로우";
        }
        return q;
    }

    public CopySearchResDto searchMoodList(HttpServletRequest httpRequest, String q, int index, Long randomSeedDto) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);

        List<CopyResDto> moodCopyRes;

        if (MoodType.containsKeyword(q)) {
            // index가 0이면 백엔드에서 랜덤 시드값 생성
            if (index == 0) {
                Long seed = new Random().nextLong(); // 랜덤 시드값 생성
                moodCopyRes = cardJpaRepository.searchMood(q);
                setScrapCnt(httpRequest, moodCopyRes);
                setIsScrap(user, moodCopyRes);
                Collections.shuffle(moodCopyRes, new Random(seed)); // 생성한 시드값으로 섞기

                // 생성한 랜덤 시드값과 함께 결과 반환
                CopySearchResDto moodSearchResult = createCopySearchResult("mood", q, moodCopyRes, index, seed);
                return moodSearchResult;
            } else if (index > 0) {
                // index가 1 이상이면 프론트엔드에서 전달한 시드값 사용
                // 프론트엔드에서 전달받은 시드값으로 섞기
                // 여기서는 seed를 프론트엔드에서 전달받는다고 가정합니다.
                Long seed = randomSeedDto; // 프론트엔드에서 전달받은 시드값
                moodCopyRes = cardJpaRepository.searchMood(q);
                setScrapCnt(httpRequest, moodCopyRes);
                setIsScrap(user, moodCopyRes);
                Collections.shuffle(moodCopyRes, new Random(seed)); // 전달받은 시드값으로 섞기

                CopySearchResDto moodSearchResult = createCopySearchResult("mood", q, moodCopyRes, index,seed);
                return moodSearchResult;
            }
        }

        // 검색 결과가 없다면
        throw new CardNotFoundException();
    }



    public CopySearchResDto searchCopyList(HttpServletRequest httpRequest, String q, int index, Long randomSeedDto) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);

        List<CopyResDto> textCopyRes;

        textCopyRes = cardJpaRepository.searchCopy(q);

        if (!textCopyRes.isEmpty()) {
            // index가 0이면 백엔드에서 랜덤 시드값 생성
            if (index == 0) {
                Long seed = new Random().nextLong(); // 랜덤 시드값 생성
                setScrapCnt(httpRequest, textCopyRes);
                setIsScrap(user, textCopyRes);
                Collections.shuffle(textCopyRes, new Random(seed)); // 생성한 시드값으로 섞기

                // 생성한 랜덤 시드값과 함께 결과 반환
                CopySearchResDto textSearchResult = createCopySearchResult("text", q, textCopyRes, index, seed);
                return textSearchResult;
            } else if (index > 0) {
                // index가 1 이상이면 프론트엔드에서 전달한 시드값 사용
                // 프론트엔드에서 전달받은 시드값으로 섞기
                // 여기서는 seed를 프론트엔드에서 전달받는다고 가정합니다.
                Long seed = randomSeedDto; // 프론트엔드에서 전달받은 시드값
                setScrapCnt(httpRequest, textCopyRes);
                setIsScrap(user, textCopyRes);
                Collections.shuffle(textCopyRes, new Random(seed)); // 전달받은 시드값으로 섞기

                CopySearchResDto textSearchResult = createCopySearchResult("text", q, textCopyRes, index,seed);
                return textSearchResult;
            }
        }

        // 검색 결과가 없다면
        throw new CardNotFoundException();
    }




    @Transactional
    public List<CopyResDto> getScrapList(HttpServletRequest httpRequest, int index) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        List<Scrap> scraps = scrapRepository.findScrapByUser(user);
        List<CopyResDto> scrapList = new ArrayList<>();

        for (Scrap scrap : scraps) {
            if (scrap.getId() == null) {
                throw new ScrapNotFoundException(); // scrap.getId()가 null이면 예외를 던집니다.
            }

            CopyResDto copyRes = createScrapRes(scrap);
            scrapList.add(copyRes);
            copyRes.setScrapTime(scrap.getCreatedAt());
        }

        // 위 반복문이 모두 실행된 후에 정렬하고 인덱스에 해당하는 스크랩만 반환합니다.
        setIsScrap(user, scrapList);
        scrapList.sort(Comparator.comparing(CopyResDto::getScrapTime).reversed());
        List<CopyResDto> result = getCopyByIndex(scrapList, index);

        return result;
    }

//
    public void createScrap(HttpServletRequest httpRequest, ScrapReqDto scrapReqDto) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        System.out.println(user.getEmail() + " createScrap");
        Card card = cardRepository.findCardById(scrapReqDto.getCardId());
        Optional<Folder> optionalFolder = folderRepository.findById(scrapReqDto.getFolderId());

        if (optionalFolder.isPresent()) {
            Folder folder = optionalFolder.get();

            if (hasScrapped(user, card)) {
                throw new DuplicateScrapException();
            }

            Scrap scrap = saveCopy(user, card); // 스크랩 저장

            // 폴더에 스크랩 추가
            Contain newContain = Contain.builder()
                    .folder(folder)
                    .scrap(scrap)
                    .build();
            containRepository.save(newContain);
        } else {
            // 폴더가 없는 경우 새로운 폴더 생성
            Folder newFolder = Folder.builder()
                    .name(scrapReqDto.getFolderName())
                    .user(user)
                    .build();
            folderRepository.save(newFolder);

            Scrap scrap = saveCopy(user, card); // 스크랩 저장

            // 새로 생성된 폴더에 스크랩 추가
            Contain newContain = Contain.builder()
                    .folder(newFolder)
                    .scrap(scrap)
                    .build();
            containRepository.save(newContain);
        }
    }


    private boolean hasScrapped(User user, Card card) {
        Scrap existingScrap = scrapRepository.findByUserAndCard(user, card);
        return existingScrap != null;
    }

    @Transactional
    public Scrap saveCopy(User user, Card card) {

        Scrap scrap = Scrap.builder()
                .user(user)
                .card(card)
                .build();

        Scrap savedScrap = scrapRepository.save(scrap);

        return savedScrap;
    }



    @Transactional
    public Long deleteScrap(HttpServletRequest httpRequest, CopyReqDto copyReq) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        Long cardId = copyReq.getCardId();
        Card card = cardRepository.findCardById(cardId);
        Scrap scrap = scrapRepository.findByUserAndCard(user, card);

        if (scrap != null) {
            scrap.setDeleteYn(1);
            return cardId;
        }

        return null;
    }

    @Transactional
    public CopyResDto createCopyRes(Card card) {
        Long id = card.getId(); // id로 넘어옴
        List<Scrap> scraps = scrapRepository.findByCardId(id);

        return CopyResDto.builder()
                .id(id)
                .brand(card.getBrand())
                .text(card.getText())
                .scrapCnt(scraps.size())
                .createdAt(card.getCreatedAt())
                .cardLink(card.getUrl())
                .build();
    }





    public CopySearchResDto createCopySearchResult(String type, String keyword, List<CopyResDto> copyResList, int index,Long seed) {
        List<CopyResDto> slicedCopyResList = getCopyByIndex(copyResList, index);

        return CopySearchResDto.builder()
                .type(type)
                .keyword(keyword)
                .totalNum(copyResList.size())
                .data(slicedCopyResList)
                .randomSeed(seed)
                .build();
    }


    @Transactional
    public CopyResDto createScrapRes(Scrap scrap) {
        Long id = scrap.getCard().getId();

        List<Scrap> scraps = scrapRepository.findByCardId(id);

        return CopyResDto.builder()
                .id(id)
                .brand(scrap.getCard().getBrand())
                .text(scrap.getCard().getText())
                .scrapCnt(scraps.size())
                .createdAt(scrap.getCard().getCreatedAt())
                .cardLink(scrap.getCard().getUrl())
                .build();
    }

    public List<CopyResDto> getCopyFilter(HttpServletRequest httpRequest, int index, CardSearchCondition condition) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        List<CopyResDto> result = cardJpaRepository.filter(condition);
        result = getCopyByIndex(result, index);
        setScrapCnt(httpRequest, result);
        setIsScrap(user,result);
        return result;
    }

    @Transactional
    public void saveCrawlingData(CrawlingReqDto crawlingReq) {
        List<CrawlingData> dataList = crawlingReq.getData();
        for (CrawlingData data : dataList) {
            Long brandId = data.getBrandId();
            Brand brand = brandRepository.findBrandById(brandId);

            Card card = Card.builder()
                    .text(data.getText())
                    .createdAt(data.getCreatedAt())
                    .brand(brand)
                    .url(data.getUrl())
                    .build();

            cardRepository.save(card);
        }
    }
    @Transactional
    public List<String> getFolderList(HttpServletRequest httpRequest) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
        List<Folder> folders = folderRepository.findByUser(user);

        List<String> folderNames = new ArrayList<>();

        for (Folder folder : folders) {
            String folderName = folder.getName();
            folderNames.add(folderName);
        }

        return folderNames;
    }




}