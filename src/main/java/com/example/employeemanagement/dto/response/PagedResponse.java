package com.example.employeemanagement.dto.response;

import lombok.*;

import java.util.List;

/**
 * Wraps paginated content with metadata.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse<T> {

    private List<T> content;
    private int     page;
    private int     size;
    private long    totalElements;
    private int     totalPages;
    private boolean last;
    private boolean first;
    private boolean empty;
}
