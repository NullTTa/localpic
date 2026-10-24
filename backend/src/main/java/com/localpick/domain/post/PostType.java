package com.localpick.domain.post;

public enum PostType {
    DAILY("일상"),
    PROMOTION("홍보"),
    EVENT("이벤트");

    private final String label;

    PostType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
