package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.dto.user.friends.FriendCardDtoResponse;
import org.springframework.data.domain.Pageable;

/**
 * Provides the interface to manage search functionality.
 *
 * @author Kovaliv Taras
 * @version 1.0
 */
public interface SearchService {
    /**
     * Method that allow you to search {@link SearchResponseDto}.
     *
     * @param searchQuery query to search
     * @return {@link SearchResponseDto}
     */
    SearchResponseDto search(String searchQuery, String languageCode);

    /**
     * Method that allow you to search {@link SearchNewsDto}.
     *
     * @param pageable    {@link Pageable}.
     * @param searchQuery query to search.
     * @return PageableDto of {@link SearchNewsDto} instances.
     */
    PageableDto<SearchNewsDto> searchAllNews(Pageable pageable, String searchQuery, String languageCode);

    /**
     * Method that allow you to search users which are not your friend yet by login or name and by page.
     * and get result as PageableAdvancedDto of{@link FriendCardDtoResponse} instances.
     *
     * @param userId The ID of the current user.
     * @param searchQuery The search query pattern for login and name field.
     * @param city The search query pattern for city field.
     * @param page parameters of to search.
     * @return PageableDto of {@link FriendCardDtoResponse} instances.
     *
     * @author Chernenko Vitaliy
     */
    PageableAdvancedDto<FriendCardDtoResponse> searchNotFriendsYet(long userId, String searchQuery, String city,Pageable page);

    /**
     * Method that allow you to search user's friends by login or name and by page.
     * and get result as PageableAdvancedDto of{@link FriendCardDtoResponse} instances.
     *
     * @param userId The ID of the current user.
     * @param page parameters of to search.
     * @return PageableDto of {@link FriendCardDtoResponse} instances.
     *
     * @author Chernenko Vitaliy
     */
    PageableAdvancedDto<FriendCardDtoResponse> searchFriends(long userId, Pageable page);
}
