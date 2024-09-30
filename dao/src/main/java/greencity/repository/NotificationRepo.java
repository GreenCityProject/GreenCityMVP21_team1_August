package greencity.repository;

import greencity.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId")
    List<Notification> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    List<Notification> findAllByUserIdAndIsReadFalse(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.receivedTime DESC LIMIT 3")
    List<Notification> findFirstThreeByUserIdOrderByReceivedTimeDesc(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.receivedTime DESC")
    List<Notification> findAllByUserIdOrderByReceivedTimeDesc(@Param("userId") Long userId);
}
