package greencity.mapping;

import greencity.dto.event.EventCreationDto;
import greencity.entity.Event;
import greencity.entity.EventDayDetails;
import greencity.enums.EventType;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class EventCreationDtoMapper extends AbstractConverter<EventCreationDto, Event> {
    @Override
    protected Event convert(EventCreationDto eventCreationDto) {
         Event event =  Event.builder()
                .eventTitle(eventCreationDto.getEventTitle())
                .description(eventCreationDto.getDescription())
                .eventType(EventType.valueOf(eventCreationDto.getEventType()))
                .images(new ArrayList<>())
                .build();

         event.setEventDayDetailsList(eventCreationDto.getEventDayDetailsList().stream()
                .map(day -> EventDayDetails.builder()
                        .id(day.getId())
                        .eventDate(day.getEventDate())
                        .eventStartTime(day.getEventStartTime())
                        .eventEndTime(day.getEventEndTime())
                        .isAllDateDuration(day.isAllDateDuration())
                        .isOnline(day.isOnline())
                        .isOffline(day.isOffline())
                        .offlinePlace(day.getOfflinePlace())
                        .onlinePlace(day.getOnlinePlace())
                        .event(event)
                        .build())
                .collect(Collectors.toSet()));

         return event;
    }
}
