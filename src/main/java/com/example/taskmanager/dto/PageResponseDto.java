package com.example.taskmanager.dto;

import lombok.Getter;

import java.util.List;
@Getter
public class PageResponseDto<T> {
    List<T> content;
    int page;
    int size;
    long totalElements;
    int totalPages;

    public PageResponseDto(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements/size);
    }
}
