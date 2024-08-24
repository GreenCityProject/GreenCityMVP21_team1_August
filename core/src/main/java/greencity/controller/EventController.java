package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.dto.event.EventCreationDto;
import greencity.dto.event.EventDto;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;


    @PostMapping(path = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EventDto> save(@RequestPart MultipartFile[] images,
                                         @RequestPart EventCreationDto eventCreationDto,
                                         @CurrentUser UserVO currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.save(images, eventCreationDto, currentUser.getId()));
    }
}
