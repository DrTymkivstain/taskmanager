package com.example.taskmanager.dto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageRequestDto {
    private final int page;
    private final int size;
    private final String sortBy;
    private final String sortOrder;

    public static PageRequestDto createPageRequestDto(HttpServletRequest req) {
        String p = req.getParameter("page");
        String s = req.getParameter("size");
        String sb = req.getParameter("sortBy");
        String so = req.getParameter("sortOrder");

        return new PageRequestDto(
                (p != null) ? Math.max(1, Integer.parseInt(p)) : 1,
                (s != null) ? Math.min(100, Math.max(1, Integer.parseInt(s))) : 10,
                (sb != null) ? sb : "id",
                (so != null) ? so.toUpperCase() : "DESC"
        );
    }
}
