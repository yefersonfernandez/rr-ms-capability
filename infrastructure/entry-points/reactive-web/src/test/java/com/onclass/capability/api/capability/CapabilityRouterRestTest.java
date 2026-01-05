package com.onclass.capability.api.capability;

import com.onclass.capability.api.config.CapabilityPath;
import com.onclass.capability.api.dto.request.CapabilityRequestDto;
import com.onclass.capability.api.dto.response.CapabilityResponseDto;
import com.onclass.capability.api.mapper.CapabilityMapper;
import com.onclass.capability.api.utils.ValidatorUtil;
import com.onclass.capability.enums.ExceptionStatusCode;
import com.onclass.capability.model.capability.Capability;
import com.onclass.capability.usecase.capability.CapabilityUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.util.List;

import static org.mockito.Mockito.when;

@TestPropertySource(properties = {"routes.paths.capabilities=/capability/api/v1/capabilities"})
@ContextConfiguration(classes = {CapabilityRouterRest.class, CapabilityHandler.class, CapabilityPath.class, ValidatorUtil.class})
@WebFluxTest
class CapabilityRouterRestTest {

    private static final String CAPABILITIES_PATH = "/capability/api/v1/capabilities";
    private static final String CAPABILITY_NAME = "Reactive Spring";
    private static final String CAPABILITY_DESCRIPTION = "A reactive framework for Spring applications";
    private static final List<Long> TECHNOLOGY_IDS = List.of(1L, 2L, 3L);
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CapabilityPath capabilityPath;

    @MockitoBean
    private CapabilityUseCase capabilityUseCase;
    @MockitoBean
    private CapabilityMapper capabilityMapper;
    @MockitoBean
    private ValidatorUtil validatorUtil;

    private CapabilityRequestDto validRequestDto;
    private Capability capability;
    private CapabilityResponseDto capabilityResponseDto;

    @BeforeEach
    void setUp() {
        validRequestDto = new CapabilityRequestDto(
                CAPABILITY_NAME,
                CAPABILITY_DESCRIPTION,
                TECHNOLOGY_IDS
        );

        capability = Capability.builder()
                .id(1L)
                .name(CAPABILITY_NAME)
                .description(CAPABILITY_DESCRIPTION)
                .technologyIds(TECHNOLOGY_IDS)
                .build();

        capabilityResponseDto = new CapabilityResponseDto(
                1L,
                CAPABILITY_NAME,
                CAPABILITY_DESCRIPTION
        );
    }

    @Test
    @DisplayName("Should load path property from CapabilityPath")
    void shouldLoadCapabilityPathProperty() {
        Assertions.assertThat(capabilityPath.getCapabilities()).isEqualTo(CAPABILITIES_PATH);
    }

    @Test
    @DisplayName("POST /capabilities - listenSaveCapability: should return 201 when capability is created")
    void post_saveCapability_shouldReturnCreated() {
        when(validatorUtil.validate(validRequestDto)).thenReturn(Mono.just(validRequestDto));
        when(capabilityMapper.toModel(validRequestDto)).thenReturn(capability);
        when(capabilityUseCase.saveCapability(capability)).thenReturn(Mono.just(capability));
        when(capabilityMapper.toCapabilityResponseDto(capability)).thenReturn(capabilityResponseDto);

        webTestClient.post()
                .uri(CAPABILITIES_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.code").isEqualTo(ExceptionStatusCode.CREATED.status())
                .jsonPath("$.data.name").isEqualTo(CAPABILITY_NAME)
                .jsonPath("$.data.description").isEqualTo(CAPABILITY_DESCRIPTION);
    }
}
