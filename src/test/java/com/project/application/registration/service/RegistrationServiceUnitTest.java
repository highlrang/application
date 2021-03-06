package com.project.application.registration.service;

import com.project.application.board.domain.Board;
import com.project.application.board.repository.BoardRepository;
import com.project.application.exception.CustomException;
import com.project.application.registration.domain.Registration;
import com.project.application.registration.domain.RegistrationStatus;
import com.project.application.registration.domain.dto.RegistrationRequestDto;
import com.project.application.registration.domain.dto.RegistrationUpdateDto;
import com.project.application.registration.repository.RegistrationRepository;
import com.project.application.user.domain.User;
import com.project.application.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.project.application.common.StatusCode.REGISTRATION_RESTRICTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceUnitTest {

    @Mock UserRepository userRepository;
    @Mock BoardRepository boardRepository;
    @Mock RegistrationRepository registrationRepository;
    @InjectMocks RegistrationServiceImpl registrationService;

    @Test @DisplayName("참여자가 작성자일 경우 저장 실패")
    public void save(){
        // given
        Long boardId = 1L;

        User givenUser = new User();
        givenUser.setId(1L);

        Board givenBoard = Board.builder().build();
        givenBoard.setWriter(givenUser);

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(givenUser));
        given(boardRepository.findById(anyLong()))
                .willReturn(Optional.of(givenBoard));

        RegistrationRequestDto dto = new RegistrationRequestDto();
        dto.setBoardId(boardId);
        dto.setUserId(givenUser.getId());

        // when - then
        assertThatThrownBy(() -> registrationService.save(dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(REGISTRATION_RESTRICTION.getMessage());
    }

    @Test @DisplayName("등록 정보 업데이트 테스트")
    public void update(){
        // given
        Long mockId = 1L;
        Registration givenRegistration = Registration.builder()
                .board(new Board())
                .user(new User()).build();
        given(registrationRepository.findById(anyLong()))
                .willReturn(Optional.of(givenRegistration));

        // when
        RegistrationUpdateDto givenDto = new RegistrationUpdateDto(mockId, RegistrationStatus.OK);
        registrationService.update(givenDto);

        // then
        assertThat(givenRegistration.getStatus())
                .isEqualTo(RegistrationStatus.OK);
    }


}
