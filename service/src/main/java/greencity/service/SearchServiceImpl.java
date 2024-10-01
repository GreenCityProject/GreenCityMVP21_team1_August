package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.dto.user.friends.FriendCardDtoResponse;
import greencity.entity.User;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final EcoNewsService ecoNewsService;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    /**
     * Method that allow you to search {@link SearchResponseDto}.
     *
     * @param searchQuery query to search
     * @return list of {@link SearchResponseDto}
     */
    @Override
    public SearchResponseDto search(String searchQuery, String languageCode) {
        PageableDto<SearchNewsDto> ecoNews = ecoNewsService.search(searchQuery, languageCode);

        return SearchResponseDto.builder()
            .ecoNews(ecoNews.getPage())
            .countOfResults(ecoNews.getTotalElements())
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<SearchNewsDto> searchAllNews(Pageable pageable, String searchQuery, String languageCode) {
        return ecoNewsService.search(pageable, searchQuery, languageCode);
    }

    @Override
    public PageableAdvancedDto<FriendCardDtoResponse> searchNotFriendsYet(long userId, String searchQuery, String city, Pageable page) {
        Page<User> notFriendsUsers;
        if (city != null && !city.isEmpty()) {
            notFriendsUsers = userRepo.getAllUsersByNameAndCityExceptMainUserAndFriends(userId, searchQuery.toLowerCase(), city.toLowerCase(), page);
        }else {
            notFriendsUsers = userRepo.getAllUsersByNameExceptMainUserAndFriends(userId, searchQuery.toLowerCase(), page);
        }
        return buildPageableAdvancedGeneticDto(notFriendsUsers, userId);
    }

    @Override
    public PageableAdvancedDto<FriendCardDtoResponse> searchFriends(long userId, Pageable page) {
        Page<User> friends = userRepo.getAllUsersFriends(userId, page);
        return buildPageableAdvancedGeneticDto(friends, userId);
    }

    private PageableAdvancedDto<FriendCardDtoResponse> buildPageableAdvancedGeneticDto(Page<User> usersPage, long userId) {
        List<FriendCardDtoResponse> friendsDto = usersPage.stream()
                .map(user -> modelMapper.map(user, FriendCardDtoResponse.class))
                .toList();

        friendsDto.forEach(friend -> friend.setMutualFriends(userRepo.getAmountOfMutualFriends(userId, friend.getId())));


        return new PageableAdvancedDto<>(
                friendsDto,
                usersPage.getTotalElements(),
                usersPage.getPageable().getPageNumber(),
                usersPage.getTotalPages(),
                usersPage.getNumber(),
                usersPage.hasPrevious(),
                usersPage.hasNext(),
                usersPage.isFirst(),
                usersPage.isLast());
    }
}
