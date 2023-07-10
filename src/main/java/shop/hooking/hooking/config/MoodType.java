package shop.hooking.hooking.config;

public enum MoodType {
    PURE("퓨어한"),
    GORGEOUS("화려한"),
    KITSCH("키치한"),
    LUXURIOUS("고급스러운"),
    NATURAL("자연의"),
    SIMPLE("심플한"),
    NATURALISTIC("네추럴한"),
    CHEERFUL("발랄한"),
    UNIQUE("독특한"),
    VIBRANT("비비드한"),
    CUTTING_EDGE("첨단의"),
    URBAN("도시적인"),
    SENSATIONAL("감각적인"),
    SHY("수줍은"),
    TRADITIONAL("전통적인"),
    FRIENDLY("친근한");

    private final String keyword;

    MoodType(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public static MoodType fromKeyword(String keyword) {
        for (MoodType moodType : MoodType.values()) {
            if (moodType.keyword.equals(keyword)) {
                return moodType;
            }
        }
        return null;
    }
}

