package com.kallan.clan.controller;

import com.kallan.clan.response.ResponseWrapper;
import com.kallan.clan.entity.Blog;
import com.kallan.clan.service.BlogService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/blogs")
@Validated
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<Blog>> createBlog(@Valid @RequestBody Blog blog) {
        try {
            Blog createdBlog = blogService.createBlog(blog);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseWrapper.success("Blog has been created successfully.", createdBlog));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseWrapper.error("An error occurred while creating the blog: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Blog>> getBlogById(@PathVariable Long id) {
        Optional<Blog> blog = Optional.ofNullable(blogService.getBlogById(id));
        return blog.map(value -> ResponseEntity.ok(ResponseWrapper.success("Blog found successfully.", value)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseWrapper.error("No blog found with the provided ID: " + id,
                                HttpStatus.NOT_FOUND.value())));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<Blog>>> getAllBlogs(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Blog> blogs = blogService.getAllBlogs(page, size);
        return ResponseEntity.ok(ResponseWrapper.success("Blogs retrieved successfully.", blogs));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Blog>> updateBlog(@PathVariable Long id,
            @RequestBody Blog updatedBlog) {
        try {
            Blog updated = blogService.updateBlog(id, updatedBlog);
            return Optional.ofNullable(updated)
                    .map(blog -> ResponseEntity.ok(ResponseWrapper.success("Blog updated successfully.", blog)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ResponseWrapper.error("No blog found with the provided ID: " + id,
                                    HttpStatus.NOT_FOUND.value())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseWrapper.error("An error occurred while updating the blog: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> deleteBlog(@PathVariable Long id) {
        try {
            boolean deleted = blogService.deleteBlog(id);
            if (deleted) {
                return ResponseEntity.ok(
                        ResponseWrapper.success("Blog has been deleted successfully.", "Deleted blog with ID: " + id));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseWrapper.error("No blog found with the provided ID: " + id,
                                HttpStatus.NOT_FOUND.value()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseWrapper.error("An error occurred while deleting the blog: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
