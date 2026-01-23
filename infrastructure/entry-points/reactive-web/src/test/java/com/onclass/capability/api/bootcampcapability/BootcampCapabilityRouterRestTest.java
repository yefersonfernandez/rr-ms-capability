package com.onclass.capability.api.bootcampcapability;

import com.onclass.capability.api.config.BootcampCapabilityPath;
import com.onclass.capability.api.dto.request.BootcampCapabilityRequestDto;
import com.onclass.capability.api.dto.response.CapabilitySummaryResponseDto;
import com.onclass.capability.api.mapper.CapabilityMapper;
import com.onclass.capability.api.utils.ValidatorUtil;
import com.onclass.capability.usecase.bootcampcapability.BootcampCapabilityUseCase;
import com.onclass.capability.model.capability.Capability;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

import static org.mockito.Mockito.when;

@TestPropertySource(properties = {
        "routes.paths.associateCapabilities=/capability/api/v1/bootcamps/associate",
        "routes.paths.getCapabilitiesByBootcampId=/capability/api/v1/bootcamps/{bootcampId}/capabilities",
        "routes.paths.deleteBootcampCascade=/capability/api/v1/bootcamps/{bootcampId}"
})
@ContextConfiguration(classes = {BootcampCapabilityRouterRest.class, BootcampCapabilityHandler.class, BootcampCapabilityPath.class, ValidatorUtil.class, CapabilityMapper.class})
@WebFluxTest
class BootcampCapabilityRouterRestTest {

    private static final String ASSOCIATE_PATH = "/capability/api/v1/bootcamps/associate";
    private static final String GET_CAPABILITIES_PATH = "/capability/api/v1/bootcamps/1/capabilities";
    private static final String DELETE_PATH = "/capability/api/v1/bootcamps/1";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BootcampCapabilityPath bootcampCapabilityPath;

    @MockitoBean
    private BootcampCapabilityUseCase bootcampCapabilityUseCase;
    @MockitoBean
    private ValidatorUtil validatorUtil;
    @MockitoBean
    private CapabilityMapper capabilityMapper;

    private BootcampCapabilityRequestDto requestDto;
    private CapabilitySummaryResponseDto summaryDto;

    @BeforeEach
    void setUp() {
        requestDto = new BootcampCapabilityRequestDto(1L, List.of(2L, 3L));
        summaryDto = new CapabilitySummaryResponseDto(2L, "Test Capability");
    }

    @Test
    @DisplayName("Should load bootcamp capability delete path property from BootcampCapabilityPath")
    void shouldLoadBootcampCapabilityDeletePathProperty() {
        Assertions.assertThat(bootcampCapabilityPath.getDeleteBootcampCascade()).isEqualTo("/capability/api/v1/bootcamps/{bootcampId}");
    }

    @Test
    @DisplayName("DELETE /bootcamps/{bootcampId} - should return 204 when deleted successfully")
    void delete_bootcampCascade_shouldReturnNoContent() {
        when(bootcampCapabilityUseCase.deleteAssociatedDataByBootcampId(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(DELETE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("POST /bootcamps/associate - should return 201 when associated successfully")
    void associateCapabilities_shouldReturnCreated() {
        when(bootcampCapabilityUseCase.associateCapabilities(requestDto.bootcampId(), requestDto.capabilityIds())).thenReturn(Mono.empty());

        webTestClient.post()
                .uri(ASSOCIATE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    @DisplayName("GET /bootcamps/{bootcampId}/capabilities - should return 200 and list of capabilities")
    void getCapabilitiesByBootcampId_shouldReturnOk() {
        var capability = Capability.builder()
                .id(2L)
                .name("Test Capability")
                .description("desc")
                .technologyIds(List.of(1L, 2L))
                .technologyCount(2)
                .build();

        when(bootcampCapabilityUseCase.getCapabilitiesByBootcampId(1L)).thenReturn(Flux.just(capability));
        when(capabilityMapper.toCapabilitySummaryResponseDto(capability)).thenReturn(summaryDto);

        webTestClient.get()
                .uri(GET_CAPABILITIES_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data[0].id").isEqualTo(2)
                .jsonPath("$.data[0].name").isEqualTo("Test Capability");
    }
}
