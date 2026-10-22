package com.localpick.domain.hashtag;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

    /** 여러 게시물의 해시태그를 한 번에: [postId, tagName] */
    @Query("select ph.post.id, h.name from PostHashtag ph join ph.hashtag h where ph.post.id in :postIds")
    List<Object[]> findTagNames(@Param("postIds") Collection<Long> postIds);

    /** 최근 사용량 많은 해시태그: [tagName, count] */
    @Query("""
            select h.name, count(ph) from PostHashtag ph join ph.hashtag h
            where ph.post.createdAt >= :since
            group by h.name
            order by count(ph) desc
            """)
    List<Object[]> findTrending(@Param("since") LocalDateTime since, Pageable pageable);

    /** 인사이트: 내 게시물에 많이 쓴 해시태그 */
    @Query("""
            select h.name, count(ph) from PostHashtag ph join ph.hashtag h
            where ph.post.writer.id = :writerId
            group by h.name
            order by count(ph) desc
            """)
    List<Object[]> findTopByWriter(@Param("writerId") Long writerId, Pageable pageable);

    @Modifying
    @Query("delete from PostHashtag ph where ph.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
