package com.onclass.capability.api.capability;

import com.onclass.capability.api.config.CapabilityPath;
import com.onclass.capability.api.dto.request.CapabilityRequestDto;
import com.onclass.capability.api.dto.response.CapabilityResponseDto;
import com.onclass.capability.api.dto.response.CapabilityWithTechnologiesResponseDto;
import com.onclass.capability.api.mapper.CapabilityMapper;
import com.onclass.capability.api.utils.ValidatorUtil;
import com.onclass.capability.enums.ExceptionStatusCode;
import com.onclass.capability.model.capability.Capability;
import com.onclass.capability.model.capability.CapabilityWithTechnologies;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestPropertySource(properties = {
        "routes.paths.capabilities=/capability/api/v1/capabilities",
        "routes.paths.capabilitiesList=/capability/api/v1/capabilities/list"
})
@ContextConfiguration(classes = {CapabilityRouterRest.class, CapabilityHandler.class, CapabilityPath.class, ValidatorUtil.class})
@WebFluxTest
class CapabilityRouterRestTest {

    public static final String PAGINATION_QUERY = "?page=0&size=10&sortBy=name&order=asc";
    private static final String CAPABILITIES_PATH = "/capability/api/v1/capabilities";
    private static final String CAPABILITY_NAME = "Reactive Spring";
    private static final String CAPABILITY_DESCRIPTION = "A reactive framework for Spring applications";
    private static final List<Long> TECHNOLOGY_IDS = List.of(1L, 2L, 3L);
    private static final int TECHNOLOGY_COUNT = 3;
    private static final String CAPABILITIES_LIST_PATH = "/capability/api/v1/capabilities/list";

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
                CAPABILITY_DESCRIPTION,
                TECHNOLOGY_COUNT
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

    @Test
    @DisplayName("GET /capabilities - listenListCapabilities: should return paginated and ordered list")
    void get_listCapabilities_shouldReturnPaginatedAndOrdered() {

        var responseDto1 = new CapabilityWithTechnologiesResponseDto(1L, "A", "descA", List.of());
        var responseDto2 = new CapabilityWithTechnologiesResponseDto(2L, "B", "descB", List.of());
        var responseDto3 = new CapabilityWithTechnologiesResponseDto(3L, "C", "descC", List.of());


        when(capabilityUseCase.getCapabilitiesWithTechnologies(0, 10, "name", "asc"))
            .thenReturn(Flux.fromIterable(List.of(
                new CapabilityWithTechnologies(1L, "A", "descA", List.of()),
                new CapabilityWithTechnologies(2L, "B", "descB", List.of()),
                new CapabilityWithTechnologies(3L, "C", "descC", List.of())
            )));
        when(capabilityMapper.toCapabilityWithTechnologiesResponseDto(any())).thenReturn(responseDto1, responseDto2, responseDto3);

        webTestClient.get()
                .uri(CAPABILITIES_LIST_PATH + PAGINATION_QUERY)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(ExceptionStatusCode.OK.status())
                .jsonPath("$.data.content[0].name").isEqualTo("A")
                .jsonPath("$.data.content[1].name").isEqualTo("B")
                .jsonPath("$.data.content[2].name").isEqualTo("C")
                .jsonPath("$.data.size").isEqualTo(10)
                .jsonPath("$.data.totalElements").isEqualTo(3);
    }

    @Test
    @DisplayName("Should load list path property from CapabilityPath")
    void shouldLoadCapabilityListPathProperty() {
        Assertions.assertThat(capabilityPath.getCapabilitiesList()).isEqualTo(CAPABILITIES_LIST_PATH);
    }
}
