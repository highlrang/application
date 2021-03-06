package com.project.application.registration.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.application.board.domain.dto.BoardRequestDto;
import com.project.application.board.domain.dto.BoardResponseDto;
import com.project.application.common.ApiResponseBody;
import com.project.application.common.StatusCode;
import com.project.application.registration.domain.RegistrationStatus;
import com.project.application.registration.domain.dto.RegistrationRequestDto;
import com.project.application.registration.domain.dto.RegistrationResponseDto;
import com.project.application.registration.repository.RegistrationRepository;
import com.project.application.registration.service.RegistrationService;
import lombok.With;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.project.application.common.StatusCode.SUCCESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class RegistrationApiControllerTest {
    @MockBean RegistrationService registrationService;
    @Autowired MockMvc mockMvc;

    @Test @DisplayName("?????? ?????? ?????????") @WithMockUser
    public void save() throws Exception {
        ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

        // given
        Long mockId = 1L;
        RegistrationRequestDto requestDto = new RegistrationRequestDto();
        requestDto.setBoardId(mockId);
        requestDto.setUserId(mockId);
        String requestString = om.writeValueAsString(requestDto);

        Long returnId = 1L;
        given(registrationService.save(any(RegistrationRequestDto.class)))
                .willReturn(returnId);

        List<RegistrationResponseDto> responseList =
                Arrays.asList(new RegistrationResponseDto(returnId, mockId, "user", RegistrationStatus.APPLY, LocalDateTime.now()));
        given(registrationService.findByBoardId(anyLong()))
                .willReturn(responseList);

        String responseString = om.writeValueAsString(
                new ApiResponseBody<>(SUCCESS.getCode(), SUCCESS.getMessage(),
                        new RegistrationApiController.RegistrationBody(responseList, returnId)
                )
        );

        // when then
        mockMvc.perform(post("/api/registration")
                        .content(requestString)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().json(responseString, false))
                .andDo(print());
    }

}
