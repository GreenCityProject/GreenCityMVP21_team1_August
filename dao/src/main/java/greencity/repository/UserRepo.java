package greencity.repository;

import greencity.dto.habit.HabitVO;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.repository.options.UserFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * Find {@link User} by email.
     *
     * @param email user email.
     * @return {@link User}
     */
    Optional<User> findByEmail(String email);

    /**
     * Find all {@link UserManagementVO}.
     *
     * @param filter   filter parameters
     * @param pageable pagination
     * @return list of all {@link UserManagementVO}
     */
    @Query(" SELECT new greencity.dto.user.UserManagementVO(u.id, u.name, u.email, u.userCredo, u.role, u.userStatus) "
        + " FROM User u ")
    Page<UserManagementVO> findAllManagementVo(UserFilter filter, Pageable pageable);

    /**
     * Find not 'DEACTIVATED' {@link User} by email.
     *
     * @param email - {@link User}'s email
     * @return found {@link User}
     * @author Vasyl Zhovnir
     */
    @Query("FROM User WHERE email=:email AND userStatus <> 1")
    Optional<User> findNotDeactivatedByEmail(String email);

    /**
     * Find id by email.
     *
     * @param email - User email
     * @return User id
     * @author Zakhar Skaletskyi
     */
    @Query("SELECT id FROM User WHERE email=:email")
    Optional<Long> findIdByEmail(String email);

    /**
     * Updates last activity time for a given user.
     *
     * @param userId               - {@link User}'s id
     * @param userLastActivityTime - new {@link User}'s last activity time
     * @author Yurii Zhurakovskyi
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastActivityTime = :userLastActivityTime WHERE u.id = :userId")
    void updateUserLastActivityTime(Long userId, Date userLastActivityTime);

    /**
     * Updates user status for a given user.
     *
     * @param userId     - {@link User}'s id
     * @param userStatus {@link String} - string value of user status to set
     */
    @Modifying
    @Transactional
    @Query("UPDATE User SET userStatus = CASE "
        + "WHEN (:userStatus = 'DEACTIVATED') THEN 1 "
        + "WHEN (:userStatus = 'ACTIVATED') THEN 2 "
        + "WHEN (:userStatus = 'CREATED') THEN 3 "
        + "WHEN (:userStatus = 'BLOCKED') THEN 4 "
        + "ELSE 0 END "
        + "WHERE id = :userId")
    void updateUserStatus(Long userId, String userStatus);

    /**
     * Find the last activity time by {@link User}'s id.
     *
     * @param userId - {@link User}'s id
     * @return {@link Date}
     */
    @Query(nativeQuery = true,
        value = "SELECT last_activity_time FROM users WHERE id=:userId")
    Optional<Timestamp> findLastActivityTimeById(Long userId);

    /**
     * Updates user rating as event organizer.
     *
     * @param userId {@link User}'s id
     * @param rate   new {@link User}'s rating as event organizer
     * @author Danylo Hlynskyi
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE User SET eventOrganizerRating=:rate WHERE id=:userId")
    void updateUserEventOrganizerRating(Long userId, Double rate);

    /**
     * Retrieves the list of the user's friends (which have INPROGRESS assign to the
     * habit).
     *
     * @param habitId {@link HabitVO} id.
     * @param userId  {@link UserVO} id.
     * @return List of friends.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM ((SELECT user_id FROM users_friends AS uf "
        + "WHERE uf.friend_id = :userId AND uf.status = 'FRIEND' AND "
        + "(SELECT count(*) FROM habit_assign ha WHERE ha.habit_id = :habitId AND ha.user_id = uf.user_id "
        + "AND ha.status = 'INPROGRESS') = 1) "
        + "UNION "
        + "(SELECT friend_id FROM users_friends AS uf "
        + "WHERE uf.user_id = :userId AND uf.status = 'FRIEND' AND "
        + "(SELECT count(*) FROM habit_assign ha WHERE ha.habit_id = :habitId AND ha.user_id = uf.friend_id "
        + "AND ha.status = 'INPROGRESS') = 1)) as ui JOIN users as u ON user_id = u.id")
    List<User> getFriendsAssignedToHabit(Long userId, Long habitId);

    /**
     * Get all user friends.
     *
     * @param userId The ID of the user.
     *
     * @return list of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM users WHERE id IN ( "
        + "(SELECT user_id FROM users_friends WHERE friend_id = :userId and status = 'FRIEND')"
        + "UNION (SELECT friend_id FROM users_friends WHERE user_id = :userId and status = 'FRIEND'));")
    List<User> getAllUserFriends(Long userId);

    Optional<User> findByName(String name);

    /**
     * Check if users already friends
     *
     * @param acceptingUserId The ID of the current user.
     * @param invitedUserId The ID of user who ask for being friends.
     *
     * @return boolean result.
     * @author Chernenko Vitaliy
     */
    @Query(nativeQuery = true, value = "SELECT CASE WHEN count(*) > 0 THEN true ELSE false END FROM users_friends " +
            "WHERE (user_id = :invitedUserId AND friend_id = :acceptingUserId) " +
            "OR (user_id = :acceptingUserId AND friend_id = :invitedUserId)")
    boolean existsFriendshipById(Long acceptingUserId, Long invitedUserId);

    /**
     * Get all user not friends except current user by name.
     *
     * @param userId The ID of the current user.
     * @param queryName The search pattern.
     * @param paging {@link Pageable}.
     *
     * @return list of {@link User}.
     *
     * @author Chernenko Vitaliy
     */
    @Query(nativeQuery = true, value = "SELECT * FROM users WHERE id NOT IN (" +
            "            (SELECT user_id FROM users_friends WHERE friend_id = :userId)" +
            "            UNION (SELECT friend_id FROM users_friends WHERE user_id = :userId)" +
            "            UNION (SELECT friend_id FROM friendship_requests where user_id = :userId))" +
            "            AND users.id != :userId" +
            "            AND (lower(name) LIKE (CONCAT('%', :queryName, '%')) OR lower(first_name) LIKE (CONCAT('%', :queryName, '%')));")
    Page<User> getAllUsersByNameExceptMainUserAndFriends(Long userId, String queryName, Pageable paging);

    /**
     * Get all user not friends except current user by name and city.
     *
     * @param userId The ID of the current user.
     * @param queryName The search query pattern for login and name field.
     * @param city The search query pattern for city field.
     * @param paging {@link Pageable}.
     *
     * @return Page of {@link User}.
     *
     * @author Chernenko Vitaliy
     */
    @Query(nativeQuery = true, value = "SELECT * FROM users WHERE id NOT IN (" +
            "            (SELECT user_id FROM users_friends WHERE friend_id = :userId)" +
            "            UNION (SELECT friend_id FROM users_friends WHERE user_id = :userId)" +
            "            UNION (SELECT friend_id FROM friendship_requests where user_id = :userId))" +
            "            AND users.id != :userId" +
            "            AND ((lower(name) LIKE (CONCAT('%', :queryName, '%')) OR lower(first_name) LIKE (CONCAT('%', :queryName, '%')))" +
            "            AND (city IS NOT NULL AND lower(city) = :city));")
    Page<User> getAllUsersByNameAndCityExceptMainUserAndFriends(Long userId, String queryName, String city, Pageable paging);

    /**
     * Get all user's friends.
     *
     * @param userId The ID of the current user.
     * @param paging {@link Pageable}.
     *
     * @return Page of {@link User}.
     *
     * @author Chernenko Vitaliy
     */
    @Query(nativeQuery = true, value = "SELECT * FROM users WHERE id IN (SELECT friend_id FROM users_friends WHERE user_id = :userId);")
    Page<User> getAllUsersFriends(Long userId, Pageable paging);

    /**
     * Get amount of mutual friends.
     *
     * @param userId The ID of the current user.
     * @param otherUserId The ID of user with whom need amount of mutual friends.
     *
     * @return amount as int.
     *
     * @author Chernenko Vitaliy
     */
    @Query(nativeQuery = true, value = "SELECT count(*)" +
            "FROM (SELECT friend_id AS user_id" +
            "      FROM users_friends WHERE user_id = :userId" +
            "      UNION" +
            "      SELECT user_id AS user_id FROM users_friends WHERE friend_id = :userId) as friends " +
            "WHERE user_id IN(SELECT friend_id AS user_id FROM users_friends" +
            "       WHERE user_id = :otherUserId" +
            "       UNION" +
            "       SELECT user_id AS user_id FROM users_friends" +
            "       WHERE friend_id = :otherUserId);")
    int getAmountOfMutualFriends(Long userId, Long otherUserId);

    /**
     * Get all users who sent invitation for friendship for current user.
     *
     * @param userId The ID of the current user.
     *
     * @return Page of {@link User}.
     *
     * @author Chernenko Vitaliy
     */
    @Query(nativeQuery = true, value = "SELECT * FROM users WHERE id IN (SELECT user_id FROM friendship_requests WHERE friend_id = :userId);")
    Page<User> getFriendshipRequestsByUserId(Long userId, Pageable paging);

    /**
     * Add user as friend.
     *
     * @param userId The ID of the current user.
     * @param friendId The ID of user who will be added as friend.
     *
     * @author Chernenko Vitaliy
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO users_friends(user_id, friend_id) VALUES(:userId, :friendId);" +
            "INSERT INTO users_friends(user_id, friend_id) VALUES(:friendId, :userId);")
    void addFriend(Long userId, Long friendId);

    /**
     * Add request for friendship.
     *
     * @param userId The ID of the current user who is making request.
     * @param friendId The ID of user who is requested be friends.
     *
     * @author Chernenko Vitaliy
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO friendship_requests(user_id, friend_id) VALUES(:userId, :friendId);")
    void addFriendshipRequest(Long userId, Long friendId);

    /**
     * Remove row from friendship_requests
     *
     * @param acceptingUserId The ID of the current user who is accepting friendship invitation.
     * @param invitedUserId The ID of user who have sent an invitation.
     *
     * @author Chernenko Vitaliy
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM friendship_requests WHERE friend_id = :acceptingUserId AND user_id = :invitedUserId " +
            "OR user_id = :acceptingUserId AND friend_id = :invitedUserId")
    int removeFromFriendshipRequestsByAcceptingUserId(Long acceptingUserId, Long invitedUserId);

    /**
     * Remove row from friendship_requests
     *
     * @param invitedUserId The ID of the current user who have sent friendship invitation.
     * @param acceptingUserId The ID of user who was invited be friends.
     *
     * @author Chernenko Vitaliy
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM friendship_requests WHERE user_id = :invitedUserId AND friend_id = :acceptingUserId")
    int removeFromFriendshipRequestsByInvitingUserId(Long invitedUserId, Long acceptingUserId);

    /**
     * Remove user from friends.
     *
     * @param userId The ID of the current user.
     * @param friendId The ID of user who will be removed from friends.
     *
     * @author Chernenko Vitaliy
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM users_friends WHERE user_id = :userId AND friend_id = :friendId " +
            "OR user_id = :friendId AND friend_id = :userId ;")
    void removeFriend(Long userId, Long friendId);
}