package com.localpick.domain.insight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface PostViewLogRepository extends JpaRepository<PostViewLog, Long> {

    List<PostViewLog> findByPostIdInAndViewedAtAfter(Collection<Long> postIds, LocalDateTime since);

    @Modifying
    @Query("delete from PostViewLog v where v.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
