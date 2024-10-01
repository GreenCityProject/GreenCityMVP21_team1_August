package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.event.AddEventCommentDtoRequest;
import greencity.dto.event.AddEventCommentDtoResponse;
import greencity.dto.event.EventCommentSendEmailDto;
import greencity.dto.eventcomment.EventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.dto.user.PlaceAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.exception.exceptions.*;
import greencity.mapping.EventCommentDtoRequestMapper;
import greencity.mapping.EventCommentResponseMapper;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static greencity.constant.AppConstant.AUTHORIZATION;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class EventCommentServiceImpl implements EventCommentService {
    private final EventCommentRepo eventCommentRepo;
    private final EventRepo eventRepo;
    private final UserRepo userRepo;
    private final HttpServletRequest httpServletRequest;
    private final RestClient restClient;
    private final EmailService emailService;
    private ModelMapper modelMapper;
    private final EventCommentDtoRequestMapper requestMapper;
    private final EventCommentResponseMapper responseMapper;
    private static final Logger logger = LoggerFactory.getLogger(EventCommentServiceImpl.class.getName());

    static Map<String, String[]> words = new HashMap<>();
    static int largestWordLength = 0;

    static {
        loadConfigs();
    }

    @Override
    public EventCommentDtoResponse saveReply(EventCommentDtoRequest commentDtoRequest, Long commentId, Long authorId, Long eventId) {
        EventComment parentComment = this.eventCommentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId));
        User user = this.userRepo.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + authorId));
        EventComment comment = this.requestMapper.toEntity(commentDtoRequest);
        comment.setAuthor(user);
        comment.setEvent(this.eventRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId")));
        comment.setParentComment(parentComment);

        EventComment savedComment = this.eventCommentRepo.save(comment);

        try {
            sendReplyNotification(parentComment, savedComment);
        } catch (MessagingException e) {
            logger.error("Failed to send email notification", e);
        }

        return this.responseMapper.toDto(savedComment);
    }

    @Override
    @Transactional
    public AddEventCommentDtoResponse addComment(Long eventId, AddEventCommentDtoRequest commentDto
            , UserVO currentUserVO) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        String filteredText = filterText(commentDto.getText(), currentUserVO.getName());
        if (filteredText.equals("This comment were blocked because you were using swear words")) {
            throw new IllegalArgumentException(filteredText);
        }

        User currentUser = modelMapper.map(currentUserVO, User.class);

        EventComment comment = EventComment.builder()
                .content(filteredText)
                .author(currentUser)
                .event(event)
                .mentionedUsers(getMentionedUsers(commentDto.getText()))
                .createdDate(LocalDateTime.now())
                .createdDate(LocalDateTime.now())
                .build();

        EventComment savedComment = eventCommentRepo.save(comment);

        //send notification to organizer(User author)
        sendNotificationToOrganizer(event, savedComment);

        AddEventCommentDtoResponse responseDto = modelMapper.map(savedComment, AddEventCommentDtoResponse.class);
        responseDto.setEventId(savedComment.getEvent().getId());
        responseDto.setUserId(savedComment.getAuthor().getId());
        responseDto.setUserName(savedComment.getAuthor().getName());
        responseDto.setCreatedDate(savedComment.getCreatedDate());
        responseDto.setModifiedDate(savedComment.getUpdatedDate());

        return responseDto;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @eventCommentServiceImpl.isOwner(#replyToCommentId, #authorId)")
    public EventCommentDtoResponse updateReply(EventCommentDtoRequest commentDtoRequest, Long replyToCommentId, Long authorId) {
        EventComment existingComment = this.eventCommentRepo.findById(replyToCommentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + replyToCommentId));

        existingComment.setContent(commentDtoRequest.getText());
        existingComment.setIsEdited(true);
        EventComment updatedComment = this.eventCommentRepo.save(existingComment);
        return this.responseMapper.toDto(updatedComment);
    }

    @Override
    public List<AddEventCommentDtoResponse> getCommentsByEventId(Long eventId) {
        if (!eventRepo.existsById(eventId)) {
            throw new EventNotFoundException("Event not found");
        }
        List<EventComment> comments = eventCommentRepo.findByEventIdOrderByCreatedDateDesc(eventId);

        return comments.stream().map(comment -> AddEventCommentDtoResponse.builder()
                .id(comment.getId())
                .text(comment.getContent())
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    public Long showQuantityOfAddedComments(Long eventId) {
        if (!eventRepo.existsById(eventId)) {
            throw new EventNotFoundException("Event not found");
        }
        return eventCommentRepo.countByEventId(eventId);
    }

    @Override
    public AddEventCommentDtoResponse getCommentById(Long commentId) {
        EventComment comment = eventCommentRepo.findById(commentId)
                .orElseThrow(() -> new EventCommentNotFoundException("Comment not found"));

        AddEventCommentDtoResponse responseDto = modelMapper.map(comment, AddEventCommentDtoResponse.class);
        responseDto.setEventId(comment.getEvent().getId());
        responseDto.setUserId(comment.getAuthor().getId());
        responseDto.setUserName(comment.getAuthor().getName());
        responseDto.setCreatedDate(comment.getCreatedDate());
        responseDto.setModifiedDate(comment.getUpdatedDate());

        return responseDto;
    }

    @Override
    public void deleteCommentById(Long eventId, Long commentId, UserVO currentUserVO) {
        if(!eventRepo.existsById(eventId)) {
            throw new EventNotFoundException("Event not found with id: " + eventId);
        } else {
            if(!eventCommentRepo.existsById(commentId) || !Objects.equals(currentUserVO.getId(),
                    eventCommentRepo.findById(commentId).get().getAuthor().getId())) {
                throw new EventCommentNotFoundException("Comment not found with id: " + commentId + " or user is not author of comment");
            } else {
                eventCommentRepo.deleteById(commentId);
            }
        }
    }

    @Override
    public String filterText(String input, String userName) {
        ArrayList<String> badWords = badWordsFound(input);

        if (!badWords.isEmpty()) {
            return "This comment were blocked because you were using swear words";
        }
        return input;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @eventCommentServiceImpl.isOwner(#replyToCommentId, #authorId)")
    public void deleteReplyById(Long replyToCommentId, Long authorId) {
        EventComment comment = this.eventCommentRepo.findById(replyToCommentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + replyToCommentId));
        this.eventCommentRepo.deleteById(replyToCommentId);
    }

    @Override
    public List<EventCommentDtoResponse> findAllReplyByCommentId(Long commentId) {
        logger.info("Finding all replies to comment with id: {}", commentId);
        if (commentId == null || commentId < 0) {
            throw new InvalidCommentIdException(ErrorMessage.INVALID_COMMENT_ID + commentId);
        }
        EventComment comment = this.eventCommentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId));
        List<EventComment> replies = this.eventCommentRepo.findAllByEventCommentId(commentId);
        return replies.stream()
                .map(this.responseMapper::toDto)
                .toList();
    }

    public boolean isOwner(Long commentId, Long userId) {
        EventComment comment = this.eventCommentRepo.findById(commentId).orElse(null);
        return comment != null && comment.getAuthor().getId().equals(userId);
    }

    private void sendReplyNotification(EventComment parentComment, EventComment replyComment) throws MessagingException {
        String parentCommentAuthorEmail = parentComment.getAuthor().getEmail();
        String subject = "Your comment has a new reply";
        String content = String.format(
                "Hello %s,<br><br>Your comment:<br>%s<br><br>has been replied by %s:<br>%s",
                parentComment.getAuthor().getName(),
                parentComment.getContent(),
                replyComment.getAuthor().getName(),
                replyComment.getContent()
        );
        this.emailService.sendEmail(parentCommentAuthorEmail, subject, content);
    }

    private static ArrayList<String> badWordsFound(String input) {
        if (input == null) {
            return new ArrayList<>();
        }
        input = input.replaceAll("1", "i")
                .replaceAll("!", "i")
                .replaceAll("3", "e")
                .replaceAll("4", "a")
                .replaceAll("@", "a")
                .replaceAll("5", "s")
                .replaceAll("7", "t")
                .replaceAll("0", "o")
                .replaceAll("9", "g");

        ArrayList<String> badWords = new ArrayList<>();
        input = input.toLowerCase().replaceAll("[^a-zA-Z]", "");

        for (int start = 0; start < input.length(); start++) {
            for (int offset = 1; offset < (input.length() + 1 - start) && offset < largestWordLength; offset++) {
                String wordToCheck = input.substring(start, start + offset);
                if (words.containsKey(wordToCheck)) {
                    String[] ignoreCheck = words.get(wordToCheck);
                    boolean ignore = false;
                    for (String ignoreWord : ignoreCheck) {
                        if (input.contains(ignoreWord)) {
                            ignore = true;
                            break;
                        }
                    }
                    if (!ignore) {
                        badWords.add(wordToCheck);
                    }
                }
            }
        }
        return badWords;
    }

    private static void loadConfigs() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://docs.google.com/spreadsheets/d/1hIEi2YG3ydav1E06Bzf2mQbGZ12kh2fe4ISgLg_UBuM/export?format=csv").openConnection().getInputStream()));
            String line = "";
            int counter = 0;
            while ((line = reader.readLine()) != null) {
                counter++;
                String[] content = null;
                try {
                    content = line.split(",");
                    if (content.length == 0) {
                        continue;
                    }
                    String word = content[0];
                    String[] ignore_in_combination_with_words = new String[]{};
                    if (content.length > 1) {
                        ignore_in_combination_with_words = content[1].split("_");
                    }

                    if (word.length() > largestWordLength) {
                        largestWordLength = word.length();
                    }
                    words.put(word.replaceAll(" ", ""), ignore_in_combination_with_words);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Loaded " + counter + " words to filter out");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<User> getMentionedUsers(String text) {
        List<User> mentionedUsers = new ArrayList<>();
        StringBuilder invalidMentions = new StringBuilder();

        Pattern mentionPattern = Pattern.compile("@([A-Za-z0-9_]+)");
        Matcher matcher = mentionPattern.matcher(text);

        while (matcher.find()) {
            String userName = matcher.group(1);

            Optional<User> mentionedUser = userRepo.findByName(userName);

            if (mentionedUser.isPresent()) {
                mentionedUsers.add(mentionedUser.get());
            } else {
                invalidMentions.append("@").append(userName).append(" ");
            }
        }
        if (!invalidMentions.isEmpty()) {
            throw new IllegalArgumentException("Can't find user with name: "
                    + invalidMentions.toString().trim());
        }
        return mentionedUsers;
    }

    private void sendNotificationToOrganizer(Event event, EventComment eventComment) {
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);

        PlaceAuthorDto placeAuthorDto = modelMapper.map(event.getAuthor(), PlaceAuthorDto.class);

        EventCommentSendEmailDto commentNotificationDto = EventCommentSendEmailDto.builder()
                .eventTitle(event.getEventTitle())
                .commentText(eventComment.getContent())
                .commentAuthor(eventComment.getAuthor().getName())
                .author(placeAuthorDto)
                .secureToken(accessToken)
                .commentDate(eventComment.getCreatedDate().toString())
                .commentId(eventComment.getId())
                .eventId(event.getId())
                .build();

        restClient.sendEventCommentNotification(commentNotificationDto);
    }
}
