package com.project.application.board.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.application.board.domain.Board;
import com.project.application.board.domain.BoardType;
import com.project.application.file.domain.dto.FileResponseDto;
import com.project.application.registration.domain.dto.RegistrationResponseDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class BoardResponseDto {
    private Long id;
    private BoardType boardType;
    private String boardTypeName;
    private String title;
    private String content;

    private Long writerId;
    private String writerName;

    private int recruitingCnt;
    private Long registrationCnt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;

    private Boolean topFix;
    private int views;
    private FileResponseDto file;
    private List<RegistrationResponseDto> registrations;

    /** 현재 접속한 사용자 정보 */
    private UserInfo userInfo;

    @Getter
    public static class UserInfo {
        private Long userId;
        private Boolean isWriter;
        private Boolean isRegistered;
        private Long registrationId;

        public UserInfo(Long userId, Boolean isWriter, Boolean isRegistered, Long registrationId){
            this.userId = userId;
            this.isWriter = isWriter;
            this.isRegistered = isRegistered;
            this.registrationId = registrationId;
        }
    }

    /** 목록용 dto */
    @QueryProjection
    public BoardResponseDto(Long id, String title,
                            Long fileId, String fileName, String filePath,
                            Boolean topFix, int views,
                            LocalDate startDate, LocalDate endDate, LocalDateTime createdDate) {
        this.id = id;
        this.title = title;
        this.file = new FileResponseDto(fileId, fileName, filePath);
        this.topFix = topFix;
        this.views = views;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdDate = createdDate;
    }

    /** 상세용 dto */
    public BoardResponseDto(Board entity){
        this.id = entity.getId();
        this.boardType = entity.getBoardType();
        this.boardTypeName = entity.getBoardType().getName();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.writerId = entity.getWriter().getId();
        this.writerName = entity.getWriter().getNickName() != null ?
                entity.getWriter().getNickName()
                : entity.getWriter().getUserId();
        this.recruitingCnt = entity.getRecruitingCnt();
        this.views = entity.getViews();
        this.topFix = entity.getTopFix();
        if(entity.getFile() != null)
            this.file = new FileResponseDto(entity.getFile());
        this.startDate = entity.getStartDate();
        this.endDate = entity.getEndDate();
        this.createdDate = entity.getCreatedDate();
    }

    public void setRegistrations(List<RegistrationResponseDto> registrations){
        this.registrations = registrations;
    }

    public void setUserInfo(Long userId){
        RegistrationResponseDto userRegistration = null;
        if(registrations != null && registrations.size() != 0)
            userRegistration = registrations.stream()
                .filter(r -> r.getUserId().equals(userId))
                .findAny()
                .orElse(null);

        Boolean isRegistered = userRegistration != null;

        this.userInfo = new UserInfo(userId
                                , writerId.equals(userId)
                                , isRegistered
                                , isRegistered ? userRegistration.getId() : null);
    }
}
