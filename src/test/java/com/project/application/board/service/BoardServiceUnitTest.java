package com.project.application.board.service;

import com.project.application.board.domain.Board;
import com.project.application.board.domain.BoardType;
import com.project.application.board.domain.dto.BoardRequestDto;
import com.project.application.board.domain.dto.BoardResponseDto;
import com.project.application.board.repository.BoardRepository;
import com.project.application.file.domain.GenericFile;
import com.project.application.file.domain.dto.FileResponseDto;
import com.project.application.file.repository.FileRepository;
import com.project.application.file.service.FileServiceLocal;
import com.project.application.registration.domain.RegistrationStatus;
import com.project.application.registration.domain.dto.RegistrationResponseDto;
import com.project.application.registration.repository.RegistrationRepository;
import com.project.application.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.project.application.user.domain.User;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BindException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class BoardServiceUnitTest {
    @Mock UserRepository userRepository;
    @Mock BoardRepository boardRepository;
    @Mock RegistrationRepository registrationRepository;
    @Mock FileRepository fileRepository;
    @Mock FileServiceLocal fileService;
    @InjectMocks BoardServiceImpl boardService;

    @Test @DisplayName("????????? ?????? ??? ????????? ?????? ?????????")
    public void requestDto(){
        BoardRequestDto dto = new BoardRequestDto();
        dto.setBoardType(BoardType.event.getName());
        dto.setWriterId(1L);
        dto.setTitle("test tile");
        dto.setContent("test content");
        LocalDate givenDate = LocalDate.now();
        dto.setStartDate(
                LocalDate.parse(
                        givenDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        )
        );
        dto.setEndDate(
                LocalDate.parse(
                        givenDate.minusDays(1L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                )
        );

        BindException exception = assertThrows(BindException.class,
                dto::toEntity);
        assertThat(exception.getAllErrors().get(0).getDefaultMessage())
                .isEqualTo("?????? ????????? ?????? ???????????? ???????????? ??????????????????");

    }

    @Test @DisplayName("????????? ?????? ???????????? ????????? ?????? ?????? ??? ????????? ??????")
    public void detail(){
        Board board = Board.builder()
                .boardType(BoardType.event)
                .title("test title")
                .content("test content")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();
        User givenWriter = new User();
        givenWriter.setId(1L);
        board.setWriter(givenWriter);
        Long boardId = 1L;

        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));
        given(registrationRepository.findAllByBoardId(any())) // null
                .willReturn(new ArrayList<>());

        BoardResponseDto boardDto = boardService.findById(boardId, null);
        int views = boardDto.getViews();

        BoardResponseDto afterBoardDto = boardService.findById(boardId, null);
        int afterViews = afterBoardDto.getViews();

        assertThat(views + 1).isEqualTo(afterViews);
    }

    /** ????????? ??????
     * ???????????? ???????????? ??? ????????? ?????? ??????
     */
    @Test @DisplayName("????????? ?????? ???????????? ???????????? ?????? ????????? ?????? ??????")
    public void detail_check_user(){
        /** given */
        Long boardId = 1L;
        User givenWriter = new User();
        givenWriter.setId(1L);
        Long userId = givenWriter.getId();

        Board givenBoard = Board.builder().boardType(BoardType.event).build();
        givenBoard.setWriter(givenWriter);

        given(boardRepository.findById(any()))
                .willReturn(Optional.of(givenBoard));
        given(registrationRepository.findAllByBoardId(any()))
                .willReturn(new ArrayList<>());

        /** when */
        BoardResponseDto result = boardService.findById(boardId, userId);

        /** then */
        BoardResponseDto.UserInfo userInfo = result.getUserInfo();
        assertThat(userInfo.getUserId()).isEqualTo(userId);
        assertThat(userInfo.getIsWriter()).isEqualTo(true);
    }

    /** ????????? ??????
     * ???????????? ?????? ?????? ???????????? ???????????? ??? ????????? ?????? ??????
     */
    @Test @DisplayName("????????? ?????? ???????????? ???????????? ?????? ?????? ????????? ?????? ??????")
    public void detail_check_user2(){
        /** given */
        Long boardId = 1L;
        Long userId = 1L;
        Long writerId = 2L;
        User givenWriter = new User();
        givenWriter.setId(writerId);

        Board givenBoard = Board.builder().boardType(BoardType.event).build();
        givenBoard.setWriter(givenWriter);

        given(boardRepository.findById(any()))
                .willReturn(Optional.of(givenBoard));
        given(registrationRepository.findAllByBoardId(any()))
                .willReturn(new ArrayList<>());

        /** when */
        BoardResponseDto result = boardService.findById(boardId, userId);

        /** then */
        BoardResponseDto.UserInfo userInfo = result.getUserInfo();
        assertThat(userInfo.getUserId()).isEqualTo(userId);
        assertThat(userInfo.getIsWriter()).isEqualTo(false);

    }

    /** ????????? ??????
     * ?????? ?????? setting ??????
     */
    @Test @DisplayName("????????? ?????? ?????? ??? ?????? ?????? ?????? ??????")
    public void detail_registrations(){
        /** given */
        Long mockId = 1L;
        User givenWriter = new User();
        givenWriter.setId(mockId);
        Board givenBoard = Board.builder().boardType(BoardType.event).build();
        givenBoard.setWriter(givenWriter);
        User givenUser = User.builder().userId("testId").build();
        List<RegistrationResponseDto> givenRegistrations = new ArrayList<>();
        givenRegistrations.add(new RegistrationResponseDto(
                mockId,
                mockId,
                givenUser.getUserId(),
                RegistrationStatus.APPLY,
                LocalDateTime.now()
        ));

        given(boardRepository.findById(anyLong()))
                .willReturn(Optional.of(givenBoard));
        given(registrationRepository.findAllByBoardId(any()))
                .willReturn(givenRegistrations);

        /** when */
        BoardResponseDto result = boardService.findById(mockId, mockId);

        /** then */
        assertThat(result.getUserInfo().getIsRegistered()).isEqualTo(true);
        assertThat(result.getRegistrations().get(0).getUserName()).isEqualTo(givenUser.getUserId());
    }

    /** ????????? ?????? */
    @Test @DisplayName("????????? ?????? ?????????")
    public void update() throws BindException {
        /** given */
        Long boardId = 1L;
        LocalDate startDate = LocalDate.now();
        int recruitingCnt = 1;
        
        Board givenBoard = Board.builder()
                .boardType(BoardType.event)
                .recruitingCnt(recruitingCnt)
                .startDate(startDate)
                .build();
        GenericFile givenFile = GenericFile.builder().build();

        given(boardRepository.findById(boardId))
                .willReturn(Optional.of(givenBoard));
        given(fileRepository.findById(anyLong()))
                .willReturn(Optional.of(givenFile));
        doNothing().when(fileRepository).deleteUnnecessary();

        /** when */
        BoardRequestDto updateDto = new BoardRequestDto();
        updateDto.setTitle("????????? ??????");
        updateDto.setRecruitingCnt(givenBoard.getRecruitingCnt() + 1);
        updateDto.setStartDate(givenBoard.getStartDate().plusDays(1L));
        LocalDate today = LocalDate.now();
        updateDto.setStartDate(today);
        updateDto.setEndDate(today);
        Long fileId = 1L;
        updateDto.setFileId(fileId);

        boardService.update(boardId, updateDto);
        
        /** then */
        assertThat(givenBoard.getTitle()).isEqualTo(updateDto.getTitle());
        assertThat(givenBoard.getRecruitingCnt()).isEqualTo(updateDto.getRecruitingCnt());
        assertThat(givenBoard.getStartDate()).isEqualTo(updateDto.getStartDate());
    }

}
