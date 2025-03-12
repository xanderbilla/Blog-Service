package com.kallan.clan.controller;

import com.kallan.clan.response.ResponseWrapper;
import com.kallan.clan.entity.Blog;
import com.kallan.clan.service.AIService;
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
    private final AIService aiService;

    public BlogController(BlogService blogService, AIService aiService) {
        this.blogService = blogService;
        this.aiService = aiService;
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<Blog>> createBlog(@Valid @RequestBody Blog blog) {
        return handleServiceCall(() -> {
            Blog createdBlog = blogService.createBlog(blog);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseWrapper.success("Your blog has been created successfully!", createdBlog));
        });
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Blog>> getBlogById(@PathVariable Long id) {
        Optional<Blog> blogOptional = Optional.ofNullable(blogService.getBlogById(id));
        return blogOptional
                .map(blog -> ResponseEntity.ok(ResponseWrapper.success("We found your blog!", blog)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseWrapper.error("Sorry, we couldn't find a blog with the ID: " + id,
                                HttpStatus.NOT_FOUND.value())));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<Blog>>> getAllBlogs(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Blog> blogs = blogService.getAllBlogs(page, size);
        return ResponseEntity.ok(ResponseWrapper.success("Here are all the blogs!", blogs));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Blog>> updateBlog(@PathVariable Long id,
            @RequestBody Blog updatedBlog) {
        return handleServiceCall(() -> {
            Blog updated = blogService.updateBlog(id, updatedBlog);
            return Optional.ofNullable(updated)
                    .map(blog -> ResponseEntity
                            .ok(ResponseWrapper.success("Your blog has been updated successfully!", blog)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ResponseWrapper.error("Sorry, we couldn't find a blog with the ID: " + id,
                                    HttpStatus.NOT_FOUND.value())));
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> deleteBlog(@PathVariable Long id) {
        return handleServiceCall(() -> {
            boolean deleted = blogService.deleteBlog(id);
            if (deleted) {
                return ResponseEntity.ok(
                        ResponseWrapper.success("Your blog has been deleted successfully!",
                                "Deleted blog with ID: " + id));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseWrapper.error("Sorry, we couldn't find a blog with the ID: " + id,
                                HttpStatus.NOT_FOUND.value()));
            }
        });
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<ResponseWrapper<String>> getBlogSummary(@PathVariable Long id) {
        Blog blog = blogService.getBlogById(id);
        Optional<Blog> blogOptional = Optional.ofNullable(blog);
        return blogOptional.map(b -> {
            String summary = aiService.generateSummary(b.getContent());
            return ResponseEntity.ok(ResponseWrapper.success("Here's the summary of your blog", summary));
        })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseWrapper.error("Sorry, we couldn't find a blog with the ID: " + id,
                                HttpStatus.NOT_FOUND.value())));
    }

    private <T> ResponseEntity<ResponseWrapper<T>> handleServiceCall(ServiceCall<T> serviceCall) {
        try {
            return serviceCall.execute();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseWrapper.error("Oops! Something went wrong: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @FunctionalInterface
    private interface ServiceCall<T> {
        ResponseEntity<ResponseWrapper<T>> execute() throws Exception;
    }
}
