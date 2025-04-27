package org.example.products.constant;

public enum SortTypeEnum {
    DEADLINE("마감순"),
    RECOMMEND("추천순"),
    RECENT("최신순");

    private final String displayName;

    SortTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
