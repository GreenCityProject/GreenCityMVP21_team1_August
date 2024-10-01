package greencity.repository;

import greencity.entity.EventDayDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface EventDayDetailsRepo extends JpaRepository<EventDayDetails, Long> {

    /**
     * Find distinct offline places (locations).
     * This method retrieves unique offline locations from the event day details.
     *
     * @return a set of unique offline places (locations).
     */
    @Query("SELECT DISTINCT e.offlinePlace FROM EventDayDetails e WHERE e.offlinePlace IS NOT NULL")
    Set<String> findDistinctLocations();
}
