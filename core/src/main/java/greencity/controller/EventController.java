package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.AppConstant;
import greencity.dto.event.*;
import greencity.annotations.ImageListSizeValidation;
import greencity.annotations.ImageSizeValidation;
import greencity.annotations.ImageValidation;
import greencity.constant.HttpStatuses;
import greencity.dto.user.UserVO;
import greencity.exception.handler.MessageResponse;
import greencity.service.EventService;
import greencity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import greencity.constant.SwaggerExampleModel;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Set;
import java.util.List;


@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final UserService userService;


    @Operation(summary = "Create new event.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Bad Request")
    })
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EventDto> save(
            @Parameter(description = SwaggerExampleModel.ADD_EVENT, required = true)
            @RequestPart @Valid EventCreationDtoRequest eventCreationDtoRequest,
            @Parameter(description = "Images of the event")
            @RequestPart(required = false) @ImageListSizeValidation(maxSize = 5) List<
                    @ImageSizeValidation(maxSizeMB = 10)
                    @ImageValidation MultipartFile> images,
            @Parameter(description = "Current User")
            @CurrentUser UserVO currentUser) {

        // Save the event
        EventDto savedEvent = this.eventService.saveEvent(eventCreationDtoRequest, images, currentUser.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

    /**
     * Method for a user to join an event.
     *
     * @param eventId the ID of the event the user wants to join.
     * @param currentUser the current user joining the event.
     * @return The updated {@link EventParticipantDto} instance after the user has joined the event.
     * @author [vulook]
     */
    @Operation(summary = "Join an event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully joined the event"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Event or user not found"),
            @ApiResponse(responseCode = "409", description = "User already joined the event")
    })
    @PostMapping("/{eventId}/join")
    public ResponseEntity<EventParticipantDto> joinEvent(
            @Parameter(description = "ID of the event to join", required = true)
            @PathVariable Long eventId,
            @Parameter(description = "Current User")
            @CurrentUser UserVO currentUser) {

        EventParticipantDto eventParticipantDto = this.eventService.joinEvent(eventId, currentUser.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(eventParticipantDto);
    }

    /**
     * Method for a user to leave an event.
     *
     * @param eventId the ID of the event the user wants to leave.
     * @param currentUser the current user leaving the event.
     * @return The updated {@link EventParticipantDto} instance after the user has left the event.
     * @author [vulook]
     */
    @Operation(summary = "Leave an event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully left the event"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Event or user not found")
    })
    @DeleteMapping("/{eventId}/leave")
    public ResponseEntity<EventParticipantDto> leaveEvent(
            @Parameter(description = "ID of the event to leave", required = true)
            @PathVariable Long eventId,
            @Parameter(description = "Current User")
            @CurrentUser UserVO currentUser) {

        EventParticipantDto eventParticipantDto = this.eventService.leaveEvent(eventId, currentUser.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(eventParticipantDto);
    }


    /**
     * Method to retrieve all events the user has joined or scheduled.
     *
     * @param currentUser the current user whose events are being retrieved.
     * @return A list of {@link EventParticipantDto} instances representing the events the user is associated with (joined or scheduled).
     * @author [vulook]
     */
    @Operation(summary = "Get all events the user has joined or scheduled.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/my-events")
    public ResponseEntity<List<EventParticipantDto>> getMyEvents(
            @Parameter(description = "Current User")
            @CurrentUser UserVO currentUser) {
        List<EventParticipantDto> events = this.eventService.getEventsUserJoinedOrScheduled(currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

    @Operation(summary = "Find filtered events based on type, location, and time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtered events retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request format"),
            @ApiResponse(responseCode = "404", description = "No events found")
    })
    @PostMapping("/filter")
    public ResponseEntity<Page<EventDto>> getFilteredEvents(
            @RequestBody EventFilterDto filterDto,
            Pageable pageable) {
        Page<EventDto> filteredEvents = this.eventService.findFilteredEvents(filterDto, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(filteredEvents);
    }

    /**
     * Method for getting all events.
     *
     * @return List of {@link EventDto} instances.
     * @author Chernenko Vitaliy
     */
    @Operation(summary = "Find all events.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping
    public ResponseEntity<Set<EventDto>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.findAll());
    }

    /**
     * Method for getting all events by its owner id.
     *
     * @return List of {@link EventDto} instances.
     * @author Chernenko Vitaliy
     */
    @Operation(summary = "Find all events by its owner id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/{userId}")
    public ResponseEntity<Set<EventDto>> getAllEventsByUser(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.findAllByUserId(userId));
    }

    public boolean isPermitted(long userId) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserVO user = userService.findByEmail(email);
        return user.getId() == userId;
    }

    @Operation(summary = "Delete event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Object> delete(@PathVariable Long eventId,
                                         @CurrentUser UserVO currentUser) {
        eventService.delete(eventId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(MessageResponse.builder()
                .message(AppConstant.DELETED).success(true).build());
    }

    @Operation(summary = "Update event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto> update(@PathVariable Long eventId,
                                           @RequestPart @Valid EventEditDto eventEditDto,
                                           @RequestPart MultipartFile[] images,
                                           @CurrentUser UserVO currentUser) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.update(eventEditDto, currentUser.getId(), eventId, images));
    }
}
