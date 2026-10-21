package com.localpick.domain.follow;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    long countByFollowingId(Long followingId);

    long countByFollowerId(Long followerId);

    /** 인사이트: 최근 N일 동안 새로 생긴 팔로워 */
    List<Follow> findByFollowingIdAndCreatedAtAfter(Long followingId, LocalDateTime since);
}
