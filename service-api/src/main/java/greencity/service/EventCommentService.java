package greencity.service;

import greencity.dto.event.AddEventCommentDtoRequest;
import greencity.dto.event.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.dto.user.UserVO;

import java.util.List;

public interface EventCommentService {
    EventCommentDtoResponse saveReply(EventCommentDtoRequest commentDtoRequest, Long commentId, Long authorId, Long eventId);

    AddEventCommentDtoResponse addComment(Long eventId, AddEventCommentDtoRequest commentDto, UserVO currentUser);

    EventCommentDtoResponse updateReply(EventCommentDtoRequest commentDtoRequest, Long replyToCommentId, Long authorId);

    List<AddEventCommentDtoResponse> getCommentsByEventId(Long eventId);

    String filterText(String input, String userName);

    void deleteReplyById(Long replyToCommentId, Long authorId);

    List<EventCommentDtoResponse> findAllReplyByCommentId(Long commentId);


    Long showQuantityOfAddedComments(Long eventId);

    AddEventCommentDtoResponse getCommentById(Long commentId);

    void deleteCommentById(Long eventId, Long commentId, UserVO currentUser);
}
