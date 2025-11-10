package com.ssafy.clonenova.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ScrollResponseDTO<T> {
    private List<T> content;
    private Long nextCursor;
    private boolean hasNext;
}
