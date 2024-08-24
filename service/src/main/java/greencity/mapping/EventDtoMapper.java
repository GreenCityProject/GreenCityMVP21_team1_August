package greencity.mapping;

import greencity.dto.event.EventDayDetailsDto;
import greencity.dto.event.EventDto;
import greencity.dto.user.AuthorDto;
import greencity.entity.Event;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class EventDtoMapper extends AbstractConverter<Event, EventDto> {
    @Override
    protected EventDto convert(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .eventTitle(event.getEventTitle())
                .description(event.getDescription())
                .eventDayDetailsList(event.getEventDayDetailsList().stream()
                        .map(day -> EventDayDetailsDto.builder()
                                .id(day.getId())
                                .eventDate(day.getEventDate())
                                .eventStartTime(day.getEventStartTime())
                                .eventEndTime(day.getEventEndTime())
                                .isAllDateDuration(day.isAllDateDuration())
                                .isOnline(day.isOnline())
                                .isOffline(day.isOffline())
                                .offlinePlace(day.getOfflinePlace())
                                .onlinePlace(day.getOnlinePlace())
                                .build())
                        .collect(Collectors.toSet()))
                .eventType(event.getEventType().toString())
                .imagePathList(event.getImages().stream()
                        .map(path -> path.getImagePath())
                        .collect(Collectors.toList()))
                .author(AuthorDto.builder()
                        .id(event.getAuthor().getId())
                        .name(event.getAuthor().getName())
                        .build())
                .build();
    }
}