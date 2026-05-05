package com.feasymenu.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "blog_posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPost extends AbstractEntity {
    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private String titleTr;
    private String titleEn;

    @Column(columnDefinition = "TEXT")
    private String excerptTr;
    @Column(columnDefinition = "TEXT")
    private String excerptEn;

    @Column(columnDefinition = "TEXT")
    private String contentTr;
    @Column(columnDefinition = "TEXT")
    private String contentEn;

}
