package com.kallan.clan.controller;

import com.kallan.clan.response.ResponseWrapper;
import com.kallan.clan.entity.Blog;
import com.kallan.clan.service.AIService;
import com.kallan.clan.service.BlogService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

    private final BlogService blogService;
    private final AIService aiService;

    public BlogController(BlogService blogService, AIService aiService) {
        this.blogService = blogService;
        this.aiService = aiService;
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<Blog>> createBlog(@Valid @RequestBody Blog blog) {
        logger.info("Creating a new blog");
        return handleServiceCall(() -> {
            Blog createdBlog = blogService.createBlog(blog);
            logger.info("Blog created successfully with ID: {}", createdBlog.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseWrapper.success("Your blog has been created successfully!", createdBlog));
        });
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Blog>> getBlogById(@PathVariable Long id) {
        logger.info("Fetching blog with ID: {}", id);
        Optional<Blog> blogOptional = Optional.ofNullable(blogService.getBlogById(id));
        return blogOptional
                .map(blog -> {
                    logger.info("Blog found with ID: {}", id);
                    return ResponseEntity.ok(ResponseWrapper.success("We found your blog!", blog));
                })
                .orElseGet(() -> {
                    logger.warn("Blog not found with ID: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ResponseWrapper.error("Sorry, we couldn't find a blog with the ID: " + id,
                                    HttpStatus.NOT_FOUND.value()));
                });
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<Blog>>> getAllBlogs(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Fetching all blogs with page: {} and size: {}", page, size);
        Page<Blog> blogs = blogService.getAllBlogs(page, size);
        logger.info("Fetched {} blogs", blogs.getTotalElements());
        return ResponseEntity.ok(ResponseWrapper.success("Here are all the blogs!", blogs));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Blog>> updateBlog(@PathVariable Long id,
            @RequestBody Blog updatedBlog) {
        logger.info("Updating blog with ID: {}", id);
        return handleServiceCall(() -> {
            Blog updated = blogService.updateBlog(id, updatedBlog);
            return Optional.ofNullable(updated)
                    .map(blog -> {
                        logger.info("Blog updated successfully with ID: {}", id);
                        return ResponseEntity
                                .ok(ResponseWrapper.success("Your blog has been updated successfully!", blog));
                    })
                    .orElseGet(() -> {
                        logger.warn("Blog not found with ID: {}", id);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ResponseWrapper.error("Sorry, we couldn't find a blog with the ID: " + id,
                                        HttpStatus.NOT_FOUND.value()));
                    });
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> deleteBlog(@PathVariable Long id) {
        logger.info("Deleting blog with ID: {}", id);
        return handleServiceCall(() -> {
            boolean deleted = blogService.deleteBlog(id);
            if (deleted) {
                logger.info("Blog deleted successfully with ID: {}", id);
                return ResponseEntity.ok(
                        ResponseWrapper.success("Your blog has been deleted successfully!",
                                "Deleted blog with ID: " + id));
            } else {
                logger.warn("Blog not found with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseWrapper.error("Sorry, we couldn't find a blog with the ID: " + id,
                                HttpStatus.NOT_FOUND.value()));
            }
        });
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<ResponseWrapper<String>> getBlogSummary(@PathVariable Long id) {
        logger.info("Generating summary for blog with ID: {}", id);
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
