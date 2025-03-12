package com.kallan.clan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kallan.clan.entity.Blog;

public interface BlogRepository extends JpaRepository<Blog, Long> {
}
