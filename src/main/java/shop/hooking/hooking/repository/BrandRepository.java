package shop.hooking.hooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.hooking.hooking.entity.Brand;
import org.springframework.stereotype.Repository;
import shop.hooking.hooking.entity.Card;
import shop.hooking.hooking.entity.Have;

import java.util.List;


@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Override
    List<Brand> findAll();

    Brand findBrandByBrandNameContaining(String q);

    Brand findByHaveId(Long have_id);

    Brand findBrandById(Long id);
}
