package shop.hooking.hooking.config.enumtype;

public enum BrandType {
    FRESHIAN("프레시안"),
    ROMAND("롬앤"),
    HERA("헤라"),
    PHYSIOGEL("피지오겔"),
    MELIXIR("멜릭서"),
    RYO("려"),
    INNISFREE("이니스프리"),
    SULWHASOO("설화수"),
    ETUDE("에뛰드"),
    MISSHA("미샤"),
    ABIB("아비브"),
    ESTRA("에스트라"),
    BENEFIT("베네피트"),
    SUM37("숨37도"),
    OHUI("오휘"),
    FMGT("fmgt"),
    NAMING("네이밍"),
    KISSME("키스미"),
    HINCE("힌스"),
    DASIQUE("데이지크"),
    AFTERBLOW("애프터 블로우"),
    THEBODYSHOP("더바디샵"),
    LONGTAKE("롱테이크"),
    AMUSE("어뮤즈"),
    TAMBURINS("탬버린즈"),
    NONFICTION("논픽션"),
    ESPOIR("에스쁘아"),
    SKINFOOD("스킨푸드");

    private final String keyword;

    BrandType(String keyword) {
        this.keyword = keyword;
    }

    public static boolean containsKeyword(String keyword) {
        for (BrandType brandType : BrandType.values()) {
            if (brandType.keyword.equals(keyword)) {
                return true;
            }
        }


        return false;
    }
}

