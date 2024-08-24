package greencity.service;

import greencity.dto.event.EventCreationDto;
import greencity.dto.event.EventDto;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {

      EventDto save(MultipartFile[] images, EventCreationDto event, Long userId);
}
