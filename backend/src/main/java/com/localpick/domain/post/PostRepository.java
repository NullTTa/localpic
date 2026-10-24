package com.localpick.domain.post;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    /* 피드 조회: 작성자(writer)를 같이 가져와 N+1 문제를 막는다 */
    @EntityGraph(attributePaths = "writer")
    Slice<Post> findSliceBy(Pageable pageable);

    @EntityGraph(attributePaths = "writer")
    Slice<Post> findByPostType(PostType postType, Pageable pageable);

    @EntityGraph(attributePaths = "writer")
    Slice<Post> findByWriterId(Long writerId, Pageable pageable);

    @EntityGraph(attributePaths = "writer")
    @Query("""
            select p from Post p
            where p.writer.id = :me
               or p.writer.id in (select f.following.id from Follow f where f.follower.id = :me)
            """)
    Slice<Post> findFollowingFeed(@Param("me") Long me, Pageable pageable);

    @EntityGraph(attributePaths = "writer")
    @Query("""
            select p from Post p
            where p.id in (select ph.post.id from PostHashtag ph where ph.hashtag.name = :tag)
            """)
    Slice<Post> findByHashtag(@Param("tag") String tag, Pageable pageable);

    @EntityGraph(attributePaths = "writer")
    Slice<Post> findByContentContaining(String keyword, Pageable pageable);

    @EntityGraph(attributePaths = "writer")
    @Query("""
            select p from Post p
            where p.postType = com.localpick.domain.post.PostType.EVENT
              and p.eventStartDate <= :today and p.eventEndDate >= :today
            order by p.eventEndDate asc
            """)
    List<Post> findOngoingEvents(@Param("today") LocalDate today, Pageable pageable);

    List<Post> findByWriterIdOrderByIdDesc(Long writerId);

    long countByWriterId(Long writerId);

    /*
     * 좋아요·댓글·조회 수는 엔티티 값을 읽어서 +1 하지 않고 DB 에서 바로 증감한다.
     * 동시에 여러 명이 눌러도 값이 덮어써지는(갱신 분실) 문제가 생기지 않는다.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Post p set p.likeCount = p.likeCount + :delta where p.id = :id")
    void addLikeCount(@Param("id") Long id, @Param("delta") long delta);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Post p set p.commentCount = p.commentCount + :delta where p.id = :id")
    void addCommentCount(@Param("id") Long id, @Param("delta") long delta);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Post p set p.viewCount = p.viewCount + 1 where p.id = :id")
    void increaseViewCount(@Param("id") Long id);
}
