package greencity.filters;

import greencity.dto.enums.EventLine;
import greencity.dto.enums.EventTime;
import greencity.entity.Event;
import greencity.entity.EventDayDetails;
import jakarta.persistence.criteria.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@Scope("prototype")
@NoArgsConstructor
@Slf4j
public class EventSpecification implements Specification<Event> {

    private transient List<SearchCriteria> searchCriteriaList;


    /**
     * Constructor to initialize the specification with search criteria.
     */
    public EventSpecification(List<SearchCriteria> searchCriteriaList) {
        this.searchCriteriaList = searchCriteriaList;
    }

    /**
     * Builds a list of {@link Predicate} based on the search criteria.
     * This method dynamically generates WHERE conditions for a query.
     *
     * @param root    The root type in the FROM clause.
     * @param query   The query that is being constructed.
     * @param builder Used to build criteria queries, expressions, and predicates.
     * @return A compound {@link Predicate} that can be applied to a query.
     */
    @Override
    public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate allPredicates = builder.conjunction();

        // Join Event with EventDayDetails to access its fields
        Join<Event, EventDayDetails> eventDayDetailsJoin = root.join("eventDayDetailsList", JoinType.INNER);

        // Apply filtering based on the search criteria list
        for (SearchCriteria searchCriteria : searchCriteriaList) {
            switch (searchCriteria.getType()) {
                case "eventLine" ->
                        allPredicates = builder.and(allPredicates, getEventLinePredicate(eventDayDetailsJoin, builder, query, (String) searchCriteria.getValue()));
                case "eventLocation" ->
                        allPredicates = builder.and(allPredicates, getEventLocationPredicate(eventDayDetailsJoin, builder, query, (String) searchCriteria.getValue()));
                case "eventTime" ->
                        allPredicates = builder.and(allPredicates, getEventTimePredicate(eventDayDetailsJoin, builder, searchCriteria.getValue()));

                default -> log.warn("Unknown search criteria type: {}", searchCriteria.getType());
            }
        }
        return allPredicates;
    }

    /**
     * Predicate for event type: ONLINE or OFFLINE
     */
    private Predicate getEventLinePredicate(Join<Event, EventDayDetails> eventDayDetailsJoin, CriteriaBuilder builder, CriteriaQuery<?> query, String eventLine) {
        // Return an empty conjunction if the value is undefined
        if (eventLine == null || eventLine.isEmpty()) {
            return builder.conjunction();
        }

        // Convert the event type string to EventTypeLine enum
        EventLine eventType = EventLine.valueOf(eventLine.toUpperCase());

        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();
        Expression<LocalDate> eventDate = eventDayDetailsJoin.get("eventDate");

        // Logic for filtering upcoming events
        switch (eventType) {
            case ONLINE -> {
                // Sort by eventDate ascending (for online events)
                query.orderBy(builder.asc(eventDayDetailsJoin.get("eventDate")));

                // Return a predicate that filters for online events and ensures the event date is in the future
                return builder.and(
                        builder.isTrue(eventDayDetailsJoin.get("isOnline")),
                        builder.greaterThanOrEqualTo(eventDate, now.toLocalDate())
                );
            }
            case OFFLINE -> {
                // Sort by offlinePlace alphabetically (for offline events)
                query.orderBy(builder.asc(eventDayDetailsJoin.get("offlinePlace")));

                // Return a predicate that filters for offline events and ensures the event date is in the future
                return builder.and(
                        builder.isTrue(eventDayDetailsJoin.get("isOffline")),
                        builder.greaterThanOrEqualTo(eventDate, now.toLocalDate())
                );
            }
            default -> {
                return builder.conjunction();
            }
        }
    }

    /**
     * Predicate for event location (single city)
     */
    private Predicate getEventLocationPredicate(Join<Event, EventDayDetails> eventDayDetailsJoin, CriteriaBuilder builder, CriteriaQuery<?> query, String eventLocation) {
        // Check if the event location is undefined or empty
        if (eventLocation == null || eventLocation.isEmpty()) {
            return builder.conjunction();
        }

        // Convert to uppercase for case-insensitive comparison
        Expression<String> offlinePlaceUpper = builder.upper(eventDayDetailsJoin.get("offlinePlace"));
        Predicate locationPredicate = builder.equal(offlinePlaceUpper, eventLocation.toUpperCase());

        // Add sorting by event date
        query.orderBy(builder.asc(eventDayDetailsJoin.get("eventDate")));

        return locationPredicate;
    }

    /**
     * Predicate for event time: FUTURE, PAST, or LIVE
     */
    private Predicate getEventTimePredicate(Join<Event, EventDayDetails> eventDayDetailsJoin, CriteriaBuilder builder, Object value) {
        // Return an empty conjunction if the value is undefined
        if (value == null) {
            return builder.conjunction();
        }

        // Convert value to EventTime enum
        EventTime eventTime = EventTime.valueOf(value.toString());
        LocalDateTime now = LocalDateTime.now();

        // Retrieve event date, start time, and end time
        Expression<LocalDate> eventDate = eventDayDetailsJoin.get("eventDate");
        Expression<LocalTime> eventStartTime = eventDayDetailsJoin.get("eventStartTime");
        Expression<LocalTime> eventEndTime = eventDayDetailsJoin.get("eventEndTime");

        // Define predicates
        Predicate futurePredicate = builder.greaterThanOrEqualTo(eventDate, now.toLocalDate());
        Predicate pastPredicate = builder.lessThan(eventDate, now.toLocalDate());
        Predicate startTimePredicate = builder.greaterThanOrEqualTo(eventStartTime, now.toLocalTime());
        Predicate endTimePredicate = builder.lessThan(eventEndTime, now.toLocalTime());

        // Return predicates (FUTURE, PAST, LIVE)
        return switch (eventTime) {
            case FUTURE -> builder.or(futurePredicate, builder.and(builder.equal(eventDate, now.toLocalDate()), startTimePredicate));
            case PAST -> builder.or(pastPredicate, builder.and(builder.equal(eventDate, now.toLocalDate()), endTimePredicate));
            case LIVE -> builder.and(
                    builder.equal(eventDate, now.toLocalDate()),
                    builder.lessThanOrEqualTo(eventStartTime, now.toLocalTime()),
                    builder.greaterThanOrEqualTo(eventEndTime, now.toLocalTime())
            );
        };
    }

}
