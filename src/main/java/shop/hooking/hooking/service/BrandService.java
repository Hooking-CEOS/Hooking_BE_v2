package shop.hooking.hooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.dto.response.BrandRes;
import shop.hooking.hooking.dto.response.ReviewRes;
import shop.hooking.hooking.entity.Brand;
import shop.hooking.hooking.entity.Review;
import shop.hooking.hooking.repository.BrandRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandService {

    private final BrandRepository brandRepository;

    public List<BrandRes.BrandDto> getBrandList(){
        List<BrandRes.BrandDto> brandDtoList = new ArrayList<>();
        List<Brand> brands = brandRepository.findAll();// 데이터베이스에서 모든 브랜드 기본정보 가져옴
        //무드 테이블에서 분위기 3개
        //카드 테이블에서 카피라이팅 랜덤 1개
        for( Brand brand : brands){
            brandDtoList.add(BrandRes.BrandDto.builder()
                    .brandId(brand.getId())
                    .brandName(brand.getBrandName())
                    .brandLink(brand.getBrandLink())
                    .build());
        }
        return  brandDtoList;

    }


    public BrandRes.BrandDetailDto getOneBrand(Long id) {
        Brand brand = brandRepository.findBrandById(id); // brand 엔티티에서 4개(아이디, 이름, 한줄소개, 링크)만 가져옴
        //무드 테이블에서 분위기 3개
        //카드 테이블에서 카피라이팅 본문 랜덤 3개
        //카드 테이블에서 카피라이팅 전체 가져오기
        BrandRes.BrandDetailDto brandDetailDto = new BrandRes.BrandDetailDto();
        brandDetailDto.builder()
                .brandId(brand.getId())
                .brandName(brand.getBrandName())
                .brandIntro(brand.getBrandIntro())
                .brandLink(brand.getBrandLink())
                .build();

        return brandDetailDto;
    }
}
