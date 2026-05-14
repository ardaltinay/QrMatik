package com.feasymenu.server.controller;

import com.feasymenu.server.model.BlogPost;
import com.feasymenu.server.repository.BlogPostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class BlogPostController {

    private final BlogPostRepository blogPostRepository;

    public BlogPostController(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    // --- PUBLIC ENDPOINTS ---

    @GetMapping("/public/blog")
    public List<BlogPost> getAllPosts() {
        return blogPostRepository.findAllByOrderByCreatedTimeDesc();
    }

    @GetMapping("/public/blog/{slug}")
    public ResponseEntity<BlogPost> getPostBySlug(@PathVariable String slug) {
        return blogPostRepository.findBySlugTr(slug).or(() -> blogPostRepository.findBySlugEn(slug))
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/public/blog/id/{id}")
    public ResponseEntity<BlogPost> getPostById(@PathVariable UUID id) {
        return blogPostRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // --- ADMIN ENDPOINTS (SUPERADMIN ONLY) ---

    @PreAuthorize("hasRole('SUPERADMIN')")
    @PostMapping("/admin/blog")
    public BlogPost createOrUpdatePost(@RequestBody BlogPost post) {
        return blogPostRepository.save(post);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @DeleteMapping("/admin/blog/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        blogPostRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
