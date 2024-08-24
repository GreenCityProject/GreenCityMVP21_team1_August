package greencity.service;

import greencity.dto.event.EventCreationDto;
import greencity.dto.event.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class EventServiceImp implements EventService {

    @Override
    public EventDto save(final MultipartFile[] images, final EventCreationDto eventCreationDto, final Long userId) {
        return null;
    }
}