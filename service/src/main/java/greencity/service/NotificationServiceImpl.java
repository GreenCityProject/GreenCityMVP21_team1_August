package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.notification.NotificationDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.entity.Notification;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.InvalidNotificationException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotificationNotFoundException;
import greencity.mapping.NotificationMapper;
import greencity.mapping.NotificationResponseDtoMapper;
import greencity.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    private final NotificationRepo notificationRepo;
    private final NotificationMapper mapper;
    private final NotificationResponseDtoMapper responseDtoMapper;

    @Override
    @Transactional
    public NotificationResponseDto save(NotificationDto notificationDto) {
        validateNotification(notificationDto);
        Notification mappedNotification = mapper.toEntity(notificationDto);
        mappedNotification.setReceivedTime(LocalDateTime.now());
        Notification savedNotification = notificationRepo.save(mappedNotification);
        return this.responseDtoMapper.toDto(savedNotification);
    }

    @Override
    public List<NotificationDto> findAllByUserId(Long userId) {
        validateId(userId);
        List<Notification> notifications = notificationRepo.findAllByUserId(userId);
        return notifications.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<NotificationDto> findAllByUserIdAndIsReadFalse(Long userId) {
        validateId(userId);
        List<Notification> notifications = notificationRepo.findAllByUserIdAndIsReadFalse(userId);
        if (notifications.isEmpty()){
            return Collections.emptyList();
        }
        return notifications.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void markAsReadNotification(Long id) {
        Notification notification = notificationRepo.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND_BY_ID + id));

        if (notification.isRead()){
            throw new BadRequestException(ErrorMessage.NOTIFICATION_ALREADY_READ);
        }
        notification.setRead(true);
        notification.setReceivedTime(LocalDateTime.now());
    }

    @Override
    public List<NotificationDto> getFirstThreeNotifications(Long userId) {
        validateId(userId);
        List<Notification> notifications = notificationRepo.findFirstThreeByUserIdOrderByReceivedTimeDesc(userId);
        return notifications.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<NotificationDto> getNotificationsSortedByReceivedTime(Long userId, boolean ascending) {
        validateId(userId);
        List<Notification> notifications = notificationRepo.findAllByUserIdOrderByReceivedTimeDesc(userId);
        Comparator<Notification> comparator = Comparator.comparing(Notification::getReceivedTime);

        if (!ascending) {
            comparator = comparator.reversed();
        }
        return notifications.stream()
                .sorted(comparator)
                .map(mapper::toDto)
                .toList();
    }

    public boolean isOwner(Long notificationId, Long userId) {
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND_BY_ID + notificationId));
        return notification.getUser().getId().equals(userId);
    }

    private void validateNotification(NotificationDto notificationDto) {
        Objects.requireNonNull(notificationDto, ErrorMessage.NOTIFICATION_DTO_CANNOT_BE_NULL);

        if (!StringUtils.hasText(notificationDto.getSection())) {
            throw new InvalidNotificationException(ErrorMessage.NOTIFICATION_SECTION_CANNOT_BE_NULL_OR_EMPTY);
        }
        if (!StringUtils.hasText(notificationDto.getSectionType())) {
            throw new InvalidNotificationException(ErrorMessage.NOTIFICATION_SECTION_TYPE_CANNOT_BE_NULL_OR_EMPTY);
        }
        if (!StringUtils.hasText(notificationDto.getText())) {
            throw new InvalidNotificationException(ErrorMessage.NOTIFICATION_TEXT_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    private void validateId(Long id) {
        Objects.requireNonNull(id, ErrorMessage.USER_ID_CANNOT_BE_NULL);
    }
}
