package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.notification.NotificationDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * Method for getting all unread notifications for current user.
     *
     * @return list of {@link NotificationDto} instances.
     * @author Zenovii Dudak.
     */
    @Operation(summary = "Get all unread notifications.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(
            @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        List<NotificationDto> notifications = notificationService.findAllByUserIdAndIsReadFalse(currentUser.getId());
        return ResponseEntity.ok(notifications);
    }

    /**
     * Method for getting all notifications for current user.
     *
     * @return list of {@link NotificationDto} instances.
     * @author Zenovii Dudak.
     */
    @Operation(summary = "Get all notifications.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/all")
    public ResponseEntity<List<NotificationDto>> getAllNotifications(
            @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        List<NotificationDto> notifications = notificationService.findAllByUserId(currentUser.getId());
        return ResponseEntity.ok(notifications);
    }

    /**
     * Method for marking notification as viewed.
     *
     * @param id - notification id.
     * @return void.
     * @author Zenovii Dudak.
     */
    @Operation(summary = "Marking notification as viewed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/markAsViewed/{id}")
    @PreAuthorize("@notificationServiceImpl.isOwner(#id, #currentUser.id)")
    public ResponseEntity<Void> markAsViewed(@PathVariable Long id, @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        notificationService.markAsReadNotification(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Method for getting first three notifications for current user.
     *
     * @return list of {@link NotificationDto} instances.
     * @author Dmytro Lysenko.
     */
    @Operation(summary = "Getting first three notifications.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/topThree")
    public ResponseEntity<List<NotificationDto>> getFirstThreeNotifications(
            @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        List<NotificationDto> notifications = notificationService.getFirstThreeNotifications(currentUser.getId());
        return ResponseEntity.ok(notifications);
    }

    /**
     * Method for getting all notifications for current user sorted by received time.
     *
     * @param ascending - if true, notifications will be sorted in ascending order, otherwise in descending.
     * @return list of {@link NotificationDto} instances.
     * @author Dmytro Lysenko.
     */
    @Operation(summary = "Getting sorted notification by received time.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/sorted")
    public ResponseEntity<List<NotificationDto>> getNotificationsSortedByReceivedTime(
            @Parameter(hidden = true) @CurrentUser UserVO currentUser,
            @RequestParam boolean ascending) {
        List<NotificationDto> notifications = notificationService.getNotificationsSortedByReceivedTime(currentUser.getId(), ascending);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "Save a notification.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping("/save")
    public ResponseEntity<NotificationResponseDto> saveNotification(@RequestBody NotificationDto notificationDto) {
        NotificationResponseDto responseDto = notificationService.save(notificationDto);
        return ResponseEntity.ok(responseDto);
    }
}
