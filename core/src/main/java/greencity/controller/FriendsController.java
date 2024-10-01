package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.friends.FriendCardDtoResponse;
import greencity.service.SearchService;
import greencity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
@Validated
public class FriendsController {
    private final SearchService searchService;
    private final UserService userService;


    @Operation(summary = "Add new friend. Send invitation to friendship.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @PostMapping("/{friendId}")
    public void addFriend(
            @Parameter(hidden = true) @CurrentUser UserVO currentUser,
            @Parameter(description = "Friend ID") @PathVariable Long friendId) {
        userService.addFriend(currentUser.getId(), friendId);
    }

    @Operation(summary = "Accept friendship invitation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @PatchMapping("/{friendId}/acceptFriend")
    public void acceptFriendshipInvitation(
            @Parameter(hidden = true) @CurrentUser UserVO currentUser,
            @Parameter(description = "Friend ID") @PathVariable Long friendId){
        userService.acceptFriendshipInvitation(currentUser.getId(), friendId);
    }

    @Operation(summary = "Cancel friendship invitation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @DeleteMapping("/{friendId}/cancelFriend")
    public void cancelFriendshipInvitation(@Parameter(hidden = true) @CurrentUser UserVO currentUser,
                                           @Parameter(description = "Friend ID")@PathVariable Long friendId) {
        userService.cancelFriendshipInvitation(currentUser.getId(), friendId);
    }

    @Operation(summary = "Decline friendship invitation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @DeleteMapping("/{friendId}/declineFriend")
    public void declineFriendshipInvitation(@Parameter(hidden = true) @CurrentUser UserVO currentUser,
                                            @Parameter(description = "Friend ID") @PathVariable Long friendId) {
        userService.declineFriendshipInvitation(currentUser.getId(), friendId);
    }

    @Operation(summary = "Delete friend.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @DeleteMapping("/{friendId}")
    public void deleteFriend(@Parameter(hidden = true) @CurrentUser UserVO currentUser,
                             @Parameter(description = "Friend ID") @PathVariable Long friendId) {
        userService.deleteFriend(currentUser.getId(), friendId);
    }

    @Operation(summary = "Get all friendship requests.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @PageableAsQueryParam
    @GetMapping("/friendRequests")
    public ResponseEntity<PageableAdvancedDto<FriendCardDtoResponse>> getFriendsRequests(
            @Parameter(hidden = true) @CurrentUser UserVO currentUser,
            @Parameter(description = "Pageable.") Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getFriendshipRequests(currentUser.getId(), pageable));
    }

    @Operation(summary = "Get all friends.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @PageableAsQueryParam
    @GetMapping
    public ResponseEntity<PageableAdvancedDto<FriendCardDtoResponse>> searchFriends(
            @Parameter(hidden = true) @CurrentUser UserVO currentUser,
            @Parameter(description = "Pageable.") Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(searchService.searchFriends(currentUser.getId(), pageable));
    }

    @Operation(summary = "Get all not friends yet.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @PageableAsQueryParam
    @GetMapping("/not-friends-yet")
    public ResponseEntity<PageableAdvancedDto<FriendCardDtoResponse>> searchNotFriendsYet(
            @Parameter(hidden = true) @CurrentUser UserVO currentUser,
            @Parameter(description = "Search name pattern.")
            @RequestParam
            @Size(min = 1, max = 30, message = "Query should be between 1 and 30 characters") String name,
            @Parameter(description = "Search city pattern.")
            @RequestParam(required = false) String city,
            @Parameter(description = "Pageable.") Pageable pageable)
    {
        return ResponseEntity.status(HttpStatus.OK).body(searchService.searchNotFriendsYet(currentUser.getId(), name, city, pageable));
    }
}
