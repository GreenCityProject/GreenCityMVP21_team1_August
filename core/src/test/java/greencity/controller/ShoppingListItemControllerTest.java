package greencity.controller;

import greencity.converters.UserArgumentResolver;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.ShoppingListItemService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.validation.Validator;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ShoppingListItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ShoppingListItemService shoppingListItemService;

    @Mock
    private Validator mockValidator;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ShoppingListItemController shoppingListItemController;
    private static final String initialUrl = "/user/shopping-list-items";
    private final Principal principal = getPrincipal();
    private final UserVO userVO = getUserVO();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(shoppingListItemController)
                .setValidator(mockValidator)
                .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper))
                .build();
    }

    @Test
    void bulkDeleteUserShoppingListItem_StatusIsOk() throws Exception {

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(initialUrl + "/user-shopping-list-items")
                        .param("ids", "1,2")
                        .principal(principal))
                .andExpect(status().isOk());

        verify(shoppingListItemService).deleteUserShoppingListItems("1,2");
    }

    @Test
    void saveShoppingListItemStatusAssignedToUser_WithoutLanguageParamTest_StatusIsCreated() throws Exception {
        List<UserShoppingListItemResponseDto> listItemResponseDtos = new ArrayList<>();

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(shoppingListItemService.saveUserShoppingListItems(anyLong(), anyLong(), anyList(), anyString()))
                .thenReturn(listItemResponseDtos);

        mockMvc.perform(post(initialUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]")
                        .param("habitId", "1")
                        .principal(principal))
                .andExpect(status().isCreated());

        verify(shoppingListItemService).saveUserShoppingListItems(userVO.getId(), 1L, new ArrayList<>(), "en");
    }

    @Test
    void updateShoppingListItemStatusAssignedToUser_WithoutLanguageParamTest_StatusIsOk() throws Exception {
        Long userShoppingListItemId = 1L;
        String status = "DONE";

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(patch(initialUrl + "/{userShoppingListItemId}/status/{status}",
                        userShoppingListItemId, status)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(shoppingListItemService).updateUserShoppingListItemStatus(userVO.getId(), userShoppingListItemId, "en", status);
    }

    @Test
    void updateShoppingListItemStatusAssignedToUser_WithLanguageParamTest_StatusIsOk() throws Exception {
        Long userShoppingListItemId = 1L;
        String status = "DONE";
        Locale locale = Locale.ENGLISH;

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(patch(initialUrl + "/{userShoppingListItemId}/status/{status}",
                        userShoppingListItemId, status)
                        .locale(locale)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(shoppingListItemService).updateUserShoppingListItemStatus(userVO.getId(), userShoppingListItemId, locale.getLanguage(), status);
    }


    @Test
    void getShoppingListItemsAssignedToUser_WithLanguageParam_StatusIsOk() throws Exception {
        Long habitId = 1L;
        Locale locale = Locale.ENGLISH;

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(initialUrl + "/habits/{habitId}/shopping-list", habitId)
                        .param("lang", locale.getLanguage())
                        .principal(principal))
                .andExpect(status().isOk());

        verify(shoppingListItemService).getUserShoppingList(userVO.getId(), habitId, locale.getLanguage());
    }

    @Test
    void geShoppingListItemAssignedToUser_WithoutLanguageParamTest_StatusIsOk() throws Exception {
        Long habitId = 1L;

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(initialUrl + "/habits/{habitId}/shopping-list", habitId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(shoppingListItemService).getUserShoppingList(userVO.getId(), habitId, "en");
    }

    @Test
    void deleteShoppingListItem_StatusIsOk() throws Exception {

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(initialUrl)
                        .param("habitId", "1")
                        .param("shoppingListItemId", "1")
                        .principal(principal))
                .andExpect(status().isOk());

        verify(shoppingListItemService).deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(1L, userVO.getId(), 1L);
    }

    @Test
    void findInProgressByUserId_StatusIsOk() throws Exception {
        Long userId = 1L;
        String languageCode = "en";
        List<ShoppingListItemDto> responseDtoList = List.of(new ShoppingListItemDto());

        when(shoppingListItemService.findInProgressByUserIdAndLanguageCode(userId, languageCode))
                .thenReturn(responseDtoList);

        mockMvc.perform(get(initialUrl + "/{userId}/get-all-inprogress", userId)
                        .param("lang", languageCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(shoppingListItemService, times(1)).findInProgressByUserIdAndLanguageCode(userId, languageCode);
    }

    @Test
    void updateShoppingListItemStatusAssignedToUser_StatusIsCreated() throws Exception {
        Long userShoppingListItemId = 1L;
        Locale locale = Locale.ENGLISH;

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(patch(initialUrl + "/{userShoppingListItemId}",
                        userShoppingListItemId)
                        .locale(locale)
                        .principal(principal))
                .andExpect(status().isCreated());

        verify(shoppingListItemService).updateUserShopingListItemStatus(userVO.getId(), userShoppingListItemId, locale.getLanguage());
    }
}
