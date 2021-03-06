package com.project.application.file.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.application.common.ApiResponseBody;
import com.project.application.file.service.FileService;
import com.project.application.file.service.FileServiceLocal;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.lang.reflect.Array;

import static com.project.application.common.StatusCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class FileApiControllerTest {
    @MockBean FileServiceLocal fileService;
    @Autowired FileApiController fileApiController;
    @Autowired MockMvc mockMvc;

    @Test
    @DisplayName("????????? ????????? ???????????? ????????? ???????????? 400 ?????? ?????????")
    @WithMockUser
    public void boardFileSaveException() throws Exception {
        /** given */
        MockMultipartFile requestFile = new MockMultipartFile("file", "test_file.txt", MediaType.TEXT_PLAIN_VALUE, "test content".getBytes());

        ApiResponseBody<?> apiResponse = new ApiResponseBody<>(VALIDATION_EXCEPTION.getCode(), VALIDATION_EXCEPTION.getMessage(), Arrays.array(ONLY_IMAGE.getMessage()));
        ObjectMapper objectMapper = new ObjectMapper();
        String result = objectMapper.writeValueAsString(apiResponse);

        /** when - then */
        mockMvc.perform(multipart("/api/file")
                        .file(requestFile)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(content().json(result, false))
                .andDo(print());

    }
}
