package com.localpick.common;

import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

/** 무한 스크롤용: 전체 개수 대신 다음 페이지가 있는지만 내려준다 */
@Getter
public class PageResponse<T> {

    private final List<T> content;
    private final int page;
    private final boolean hasNext;

    public PageResponse(List<T> content, int page, boolean hasNext) {
        this.content = content;
        this.page = page;
        this.hasNext = hasNext;
    }

    public static <T> PageResponse<T> of(Slice<?> slice, List<T> content) {
        return new PageResponse<>(content, slice.getNumber(), slice.hasNext());
    }
}
