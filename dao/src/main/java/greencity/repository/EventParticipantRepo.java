package greencity.repository;

import greencity.entity.Event;
import greencity.entity.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipantRepo extends JpaRepository<EventParticipant, Long>, JpaSpecificationExecutor<Event> {
    /**
     * Find all events where the user is a participant or author.
     *
     * @param userId the ID of the user whose events are to be found.
     * @return List of EventParticipant where the user is a participant or author.
     */
    List<EventParticipant> findAllByUserId(Long userId);

    /**
     * Method to find an EventParticipant by event and user ID.
     *
     * @param event The event to search for.
     * @param userId The ID of the user to search for.
     * @return An Optional of EventParticipant if found, otherwise empty.
     */
    Optional<EventParticipant> findByEventAndUserId(Event event, Long userId);
}
