package greencity.service;

import greencity.dto.event.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Set;

public interface EventService {
      /**
       * Method saving new event.
       *
       * @param eventCreationDtoRequest The dto for creating new event {@link EventCreationDtoRequest}
       * @param images                  list of {@link MultipartFile}
       * @param userEmail               Represents user email.
       * @return set of {@link EventDto} instances which belong to user.
       * @author [vulook]
       */
      EventDto saveEvent(EventCreationDtoRequest eventCreationDtoRequest, List<MultipartFile> images, String userEmail);

      /**
       * Method for a user to join an event.
       *
       * @param eventId   the ID of the event to join.
       * @param userEmail the email of the user joining the event.
       * @return EventDto with the updated event details.
       * @author [vulook]
       */
      EventParticipantDto joinEvent(Long eventId, String userEmail);

      /**
       * Method for a user to leave an event.
       *
       * @param eventId   the ID of the event.
       * @param userEmail the email of the user leaving the event.
       * @return EventDto with the updated event details.
       * @author [vulook]
       */
      EventParticipantDto leaveEvent(Long eventId, String userEmail);

      /**
       * Method to get all events the user joined or scheduled.
       *
       * @param userId the id of the user whose events are being retrieved.
       * @return List of EventDto representing events the user is associated with.
       * @author [vulook]
       */
      List<EventParticipantDto> getEventsUserJoinedOrScheduled(Long userId);

      /**
       * Get distinct locations for all events.
       *
       * @return a set of distinct locations (cities) for all events.
       * @author [vulook]
       */
      Set<String> getDistinctLocations();

      /**
       * Method to find filtered events for a specific user based on event time category (FUTURE, PAST, or LIVE)
       * and other filtering criteria like event type (ONLINE or OFFLINE) and location (city).
       *
       * @param filterDto the DTO containing filter criteria such as event type, location, cities, and time category.
       * @param pageable  pagination information for the results.
       * @return A paginated list of EventDto representing filtered events based on the criteria.
       * @author [vulook]
       */
      Page<EventDto> findFilteredEvents(EventFilterDto filterDto, Pageable pageable);



      /**
       * Method for getting user's events by user id.
       *
       * @param userId user id.
       * @return set of {@link EventDto} instances which belong to user.
       * @author Chernenko Vitaliy
       */
      Set<EventDto> findAllByUserId(Long userId);

      /**
       * Method for getting all events.
       *
       * @author Chernenko Vitaliy
       */

      void delete(Long eventId, Long userId);

      EventDto update(EventEditDto event, Long userId, Long eventId, MultipartFile[] images);

      /**
       * Method for getting all events.
       *
       * @return set of {@link EventDto} instances.
       *
       * @author Chernenko Vitaliy
       */
      Set<EventDto> findAll();
}

