package greencity.service;

import greencity.client.RestClient;
import greencity.dto.event.AddEventCommentDtoRequest;
import greencity.dto.event.AddEventCommentDtoResponse;
import greencity.dto.event.EventCommentSendEmailDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.exception.exceptions.EventCommentNotFoundException;
import greencity.exception.exceptions.EventNotFoundException;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static greencity.constant.AppConstant.AUTHORIZATION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventCommentServiceImplTest {
    @Mock
    private EventCommentRepo eventCommentRepo;

    @Mock
    private EventRepo eventRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private RestClient restClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EventCommentServiceImpl eventCommentServiceImpl;

    private Long eventId;
    private Long commentId;
    private AddEventCommentDtoRequest commentDto;
    private UserVO userVO;
    private Event event;
    private User user;
    private EventComment eventComment;
    private AddEventCommentDtoResponse responseDto;
    private PlaceAuthorDto placeAuthorDto;

    @BeforeEach
    public void setup() {
        eventId = 1L;
        commentId = 1L;
        commentDto = new AddEventCommentDtoRequest();
        commentDto.setText("Comment text");

        userVO = new UserVO();
        user = new User();
        event = new Event();
        event.setId(eventId);
        event.setAuthor(user);

        eventComment = EventComment.builder()
                .content("Comment text")
                .author(user)
                .event(event)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        responseDto = new AddEventCommentDtoResponse();
        responseDto.setEventId(eventId);
        responseDto.setText("Comment text");

        placeAuthorDto = new PlaceAuthorDto();

        try {
            Method loadConfigsMethod = EventCommentServiceImpl.class.getDeclaredMethod("loadConfigs");
            loadConfigsMethod.setAccessible(true);
            loadConfigsMethod.invoke(null);
        } catch (Exception e) {
            fail("Failed to invoke loadConfigs method: " + e.getMessage());
        }
    }

    private void mockCommonEventMethods() {
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(eventCommentRepo.save(any(EventComment.class))).thenReturn(eventComment);
        when(modelMapper.map(eventComment, AddEventCommentDtoResponse.class)).thenReturn(responseDto);
        when(modelMapper.map(event.getAuthor(), PlaceAuthorDto.class)).thenReturn(placeAuthorDto);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn("Bearer test-token");
    }

    @Test
    void addComment() {
        mockCommonEventMethods();

        AddEventCommentDtoResponse result = eventCommentServiceImpl.addComment(eventId, commentDto, userVO);

        assertNotNull(result);
        assertEquals("Comment text", result.getText());
        assertEquals(eventId, result.getEventId());
        verify(eventCommentRepo, times(1)).save(any(EventComment.class));
    }

    @Test
    void showQuantityOfAddedComments() {
        Long expectedCount = 5L;

        when(eventRepo.existsById(eventId)).thenReturn(true);
        when(eventCommentRepo.countByEventId(eventId)).thenReturn(expectedCount);

        Long result = eventCommentServiceImpl.showQuantityOfAddedComments(eventId);

        assertNotNull(result);
        assertEquals(expectedCount, result);
        verify(eventRepo, times(1)).existsById(eventId);
        verify(eventCommentRepo, times(1)).countByEventId(eventId);
    }

    @Test
    public void showQuantityOfAddedComments_EventNotFound() {
        when(eventRepo.existsById(eventId)).thenReturn(false);

        assertThrows(EventNotFoundException.class, () -> {
            eventCommentServiceImpl.showQuantityOfAddedComments(eventId);
        });

        verify(eventRepo, times(1)).existsById(eventId);
        verify(eventCommentRepo, times(0)).countByEventId(anyLong());
    }

    @Test
    public void getCommentById_Success() {
        when(eventCommentRepo.findById(commentId)).thenReturn(Optional.of(eventComment));
        when(modelMapper.map(eventComment, AddEventCommentDtoResponse.class)).thenReturn(responseDto);

        AddEventCommentDtoResponse result = eventCommentServiceImpl.getCommentById(commentId);

        assertNotNull(result);
        verify(eventCommentRepo, times(1)).findById(commentId);
        verify(modelMapper, times(1)).map(eventComment, AddEventCommentDtoResponse.class);
    }

    @Test
    public void getCommentById_CommentNotFound() {
        when(eventCommentRepo.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(EventCommentNotFoundException.class, () -> {
            eventCommentServiceImpl.getCommentById(commentId);
        });

        verify(eventCommentRepo, times(1)).findById(commentId);
        verify(modelMapper, times(0)).map(any(EventComment.class), eq(AddEventCommentDtoResponse.class));
    }

    @Test
    public void getCommentsByEventId() {
        when(eventRepo.existsById(eventId)).thenReturn(true);
        when(eventCommentRepo.findByEventIdOrderByCreatedDateDesc(eventId))
                .thenReturn(Collections.singletonList(eventComment));

        List<AddEventCommentDtoResponse> result = eventCommentServiceImpl.getCommentsByEventId(eventId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Comment text", result.get(0).getText());
        verify(eventRepo, times(1)).existsById(eventId);
        verify(eventCommentRepo, times(1)).findByEventIdOrderByCreatedDateDesc(eventId);
    }

    @Test
    public void getCommentsByEventId_ShouldThrowEventNotFoundException() {
        when(eventRepo.existsById(eventId)).thenReturn(false);

        assertThrows(EventNotFoundException.class, () -> {
            eventCommentServiceImpl.getCommentsByEventId(eventId);
        });

        verify(eventRepo, times(1)).existsById(eventId);
        verify(eventCommentRepo, times(0)).findByEventIdOrderByCreatedDateDesc(eventId);
    }

    @Test
    public void addComment_WithNotification_Success() {
        mockCommonEventMethods();

        AddEventCommentDtoResponse result = eventCommentServiceImpl.addComment(eventId, commentDto, userVO);

        assertNotNull(result);
        assertEquals("Comment text", result.getText());
        assertEquals(eventId, result.getEventId());

        verify(restClient, times(1)).sendEventCommentNotification(any(EventCommentSendEmailDto.class));
        verify(eventCommentRepo, times(1)).save(any(EventComment.class));
    }

    @Test
    public void getMentionedUsers() throws Exception {
        String text = "@Nazar and @Oleg";
        User john = new User();
        john.setName("Nazar");
        User jane = new User();
        jane.setName("Oleg");

        when(userRepo.findByName("Nazar")).thenReturn(Optional.of(john));
        when(userRepo.findByName("Oleg")).thenReturn(Optional.of(jane));

        Method method = EventCommentServiceImpl.class.getDeclaredMethod("getMentionedUsers", String.class);
        method.setAccessible(true);
        List<User> result = (List<User>) method.invoke(eventCommentServiceImpl, text);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(john));
        assertTrue(result.contains(jane));
        verify(userRepo, times(1)).findByName("Nazar");
        verify(userRepo, times(1)).findByName("Oleg");
    }

    @Test
    public void filterText_WithBadWords_ShouldReturnBlockedMessage() {
        String inputText = "anita";//this is some bad word
        String userName = "JohnDoe";

        String result = eventCommentServiceImpl.filterText(inputText, userName);

        assertEquals("This comment were blocked because you were using swear words", result);
    }
}
