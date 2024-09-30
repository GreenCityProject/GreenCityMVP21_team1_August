package greencity.repository;

import greencity.entity.EventComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventCommentRepo extends JpaRepository<EventComment, Long> {
    List<EventComment> findByEventIdOrderByCreatedDateDesc(Long eventId);

    List<EventComment> findByParentCommentIdOrderByCreatedDateDesc(Long parentCommentId);

    @Query("SELECT COUNT(c) FROM EventComment c WHERE c.event.id = :eventId")
    Long countByEventId(Long eventId);
}