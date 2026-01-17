package io.github.zlemiesz.springemployeeservice.dto.common;

import java.util.List;

/**
 * @author Zbigniew Lemiesz
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}