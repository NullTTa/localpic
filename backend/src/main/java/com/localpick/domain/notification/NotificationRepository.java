package com.localpick.domain.notification;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @EntityGraph(attributePaths = "sender")
    List<Notification> findTop30ByReceiverIdOrderByIdDesc(Long receiverId);

    long countByReceiverIdAndReadFalse(Long receiverId);

    @Modifying(clearAutomatically = true)
    @Query("update Notification n set n.read = true where n.receiver.id = :receiverId and n.read = false")
    int markAllRead(@Param("receiverId") Long receiverId);

    @Modifying
    @Query("delete from Notification n where n.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
