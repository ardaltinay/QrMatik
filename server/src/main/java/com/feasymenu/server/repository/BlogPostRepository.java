package com.feasymenu.server.repository;

import com.feasymenu.server.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BlogPostRepository extends JpaRepository<BlogPost, UUID> {
    Optional<BlogPost> findBySlugTr(String slugTr);
    Optional<BlogPost> findBySlugEn(String slugEn);

    List<BlogPost> findAllByOrderByCreatedTimeDesc();
}
