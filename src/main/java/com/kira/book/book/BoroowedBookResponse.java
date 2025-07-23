package com.kira.book.book;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoroowedBookResponse {
    private Integer id;
    private String title;
    private String authorName;
    private String isbn;


    private Double rate;
    private Boolean returned;
    private boolean returnApproved;
}
