package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.friends.FriendCardDtoResponse;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import org.springframework.data.domain.Pageable;
import java.util.Date;
import java.util.Optional;

public interface UserService {
    /**
     * Method that allow you to find not 'DEACTIVATED' {@link UserVO} by email.
     *
     * @param email - {@link UserVO}'s email
     * @return {@link Optional} of found {@link UserVO}.
     * @author Vasyl Zhovnir
     */
    Optional<UserVO> findNotDeactivatedByEmail(String email);

    /**
     * Find UserVO's id by UserVO email.
     *
     * @param email - {@link UserVO} email
     * @return {@link UserVO} id
     * @author Zakhar Skaletskyi
     */
    Long findIdByEmail(String email);

    /**
     * Updates last activity time for a given user.
     *
     * @param userId               - {@link UserVO}'s id
     * @param userLastActivityTime - new {@link UserVO}'s last activity time
     * @author Yurii Zhurakovskyi
     */
    void updateUserLastActivityTime(Long userId, Date userLastActivityTime);

    /**
     * Method that allow you to find {@link UserVO} by id.
     *
     * @param id a value of {@link Long}
     * @return {@link UserVO} with this id.
     */
    UserVO findById(Long id);

    /**
     * Method that allow you to find {@link UserVO} by email.
     *
     * @param email a value of {@link String}
     * @return {@link UserVO} with this email.
     */
    UserVO findByEmail(String email);

    /**
     * Update status of user.
     *
     * @param id         {@link UserVO} id.
     * @param userStatus {@link UserStatus} for user.
     * @return {@link UserStatusDto}
     */
    UserStatusDto updateStatus(Long id, UserStatus userStatus, String email);

    /**
     * Update {@code ROLE} of user.
     *
     * @param id    {@link UserVO} id.
     * @param role  {@link Role} for user.
     * @param email Email of the user.
     * @return {@link UserRoleDto}
     */
    @Deprecated
    UserRoleDto updateRole(Long id, Role role, String email);

    /**
     * The method checks by id if a {@link UserVO} is online.
     *
     * @param userId - {@link UserVO}'s id
     * @return {boolean} is user online
     */
    boolean checkIfTheUserIsOnline(Long userId);

    /**
     * Method that returns {@link String} initials (first one or two letters of
     * name) of user.
     *
     * @param userId - {@link UserVO}'s id
     * @return {@link String} user's initials
     */
    String getInitialsById(Long userId);

    /**
     * Method for updating user event organizer rating.
     *
     * @param userId - {@link UserVO}'s id
     * @param rate   - new user event organizer rating
     *
     */
    void updateEventOrganizerRating(Long userId, Double rate);

    /**
     * Method that returns list of users filtered by criteria.
     *
     * @param criteria value which we used to filter users.
     */
    PageableDto<UserManagementVO> getAllUsersByCriteria(String criteria, String role, String status, Pageable pageable);

    /**
     * Method for adding new friend.
     *
     * @param userId The ID of user who add friend.
     * @param friendId The ID of user who should be added as friend.
     *
     * @author Chernenko Vitaliy
     */
    void addFriend(Long userId, Long friendId);

    /**
     * Method for accepting friendship invitation.
     *
     * @param userId The ID of current user.
     * @param friendId The ID of user whose invitation need to accept.
     *
     * @author Chernenko Vitaliy
     */
    void acceptFriendshipInvitation(Long userId, Long friendId);

    /**
     * Method for canceling friendship request.
     *
     * @param userId The ID of current user.
     * @param friendId The ID of user request to who need to cancel.
     *
     * @author Chernenko Vitaliy
     */
    void cancelFriendshipInvitation(Long userId, Long friendId);

    /**
     * Method for declining friendship invitation.
     *
     * @param userId The ID of current user.
     * @param friendId The ID of user whose invitation need to decline.
     *
     * @author Chernenko Vitaliy
     */
    void declineFriendshipInvitation(Long userId, Long friendId);

    /**
     * Method for deleting user from friends.
     *
     * @param userId The ID of current user.
     * @param friendId The ID of user who has to be removed from friends.
     *
     * @author Chernenko Vitaliy
     */
    void deleteFriend(Long userId, Long friendId);

    PageableAdvancedDto<FriendCardDtoResponse> getFriendshipRequests(long userId, Pageable page);
}
