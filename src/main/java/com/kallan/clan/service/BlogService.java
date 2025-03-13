package com.kallan.clan.service;

import com.kallan.clan.entity.Blog;
import com.kallan.clan.exception.ResourceNotFoundException;
import com.kallan.clan.repository.BlogRepository;

import java.time.LocalDateTime;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    public Page<Blog> getAllBlogs(int page, int size) {
        return blogRepository.findAll(PageRequest.of(page, size));
    }

    @Cacheable(value = "blogs", key = "#id")
    public Blog getBlogById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + id));
    }

    @Transactional
    @CachePut(value = "blogs", key = "#id")
    public Blog updateBlog(Long id, Blog updatedBlog) {
        return blogRepository.findById(id).map(existingBlog -> {
            updateBlogFields(existingBlog, updatedBlog);
            existingBlog.setUpdatedAt(LocalDateTime.now());
            return blogRepository.save(existingBlog);
        }).orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + id));
    }

    @Transactional
    @CacheEvict(value = "blogs", key = "#id")
    public boolean deleteBlog(Long id) {
        if (!blogRepository.existsById(id)) {
            throw new ResourceNotFoundException("Blog not found with id: " + id);
        }
        blogRepository.deleteById(id);
        return true;
    }

    private void updateBlogFields(Blog existingBlog, Blog updatedBlog) {
        if (updatedBlog.getTitle() != null && !updatedBlog.getTitle().equals(existingBlog.getTitle())) {
            existingBlog.setTitle(updatedBlog.getTitle());
        }
        if (updatedBlog.getContent() != null && !updatedBlog.getContent().equals(existingBlog.getContent())) {
            existingBlog.setContent(updatedBlog.getContent());
        }
        if (updatedBlog.getAuthor() != null && !updatedBlog.getAuthor().equals(existingBlog.getAuthor())) {
            existingBlog.setAuthor(updatedBlog.getAuthor());
        }
    }
}
