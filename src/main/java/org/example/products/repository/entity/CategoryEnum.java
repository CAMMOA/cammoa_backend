package org.example.products.repository.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CategoryEnum {
    FOOD("식품"),
    WATER_DRINK("생수·음료"),
    LIVING("생활용품"),
    STATIONERY("문구류"),
    BEAUTY("화장품");

    private final String displayName;

    CategoryEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static CategoryEnum from(String value) {
        for (CategoryEnum category : CategoryEnum.values()) {
            if (category.displayName.equals(value)) { // 한글이랑 매칭
                return category;
            }
            if (category.name().equalsIgnoreCase(value)) { // 영어(enum 이름)이랑 매칭
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + value);
    }
}
