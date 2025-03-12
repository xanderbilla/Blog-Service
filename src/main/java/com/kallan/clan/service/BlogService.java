package com.kallan.clan.service;

import com.kallan.clan.entity.Blog;
import com.kallan.clan.exception.ResourceNotFoundException;
import com.kallan.clan.repository.BlogRepository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlogService {

    private final BlogRepository blogRepository;

    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Transactional
    public Blog createBlog(Blog blog) {
        blog.setCreatedAt(LocalDateTime.now());
        return blogRepository.save(blog);
    }

    // Retrieve all blogs
    public Page<Blog> getAllBlogs(int page, int size) {
        return blogRepository.findAll(PageRequest.of(page, size));
    }

    // Retrieve a blog by ID
    public Blog getBlogById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + id));
    }

    @Transactional
    public Blog updateBlog(Long id, Blog updatedBlog) {
        return blogRepository.findById(id).map(existingBlog -> {
            boolean isUpdated = false;

            // Preserve existing values if not provided in request
            String newTitle = updatedBlog.getTitle() != null ? updatedBlog.getTitle() : existingBlog.getTitle();
            String newContent = updatedBlog.getContent() != null ? updatedBlog.getContent() : existingBlog.getContent();
            String newAuthor = updatedBlog.getAuthor() != null ? updatedBlog.getAuthor() : existingBlog.getAuthor();

            if (!existingBlog.getTitle().equals(newTitle)) {
                existingBlog.setTitle(newTitle);
                isUpdated = true;
            }
            if (!existingBlog.getContent().equals(newContent)) {
                existingBlog.setContent(newContent);
                isUpdated = true;
            }
            if (!existingBlog.getAuthor().equals(newAuthor)) {
                existingBlog.setAuthor(newAuthor);
                isUpdated = true;
            }

            if (isUpdated) {
                existingBlog.setUpdatedAt(LocalDateTime.now());
                return blogRepository.save(existingBlog);
            }
            return existingBlog;
        }).orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + id));
    }

    @Transactional
    public boolean deleteBlog(Long id) {
        if (!blogRepository.existsById(id)) {
            throw new ResourceNotFoundException("Blog not found with id: " + id);
        }
        blogRepository.deleteById(id);
        return true;
    }
}