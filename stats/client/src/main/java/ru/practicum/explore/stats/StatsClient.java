package ru.practicum.explore.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.explore.Constants.DATE_FORMAT;

@Slf4j
@Service
public class StatsClient {

    protected final RestTemplate rest;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {

        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void addHit(HitDto hitDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<HitDto> requestEntity = new HttpEntity<>(hitDto, headers);
        rest.exchange("/hit", HttpMethod.POST, requestEntity, Object.class);
    }

    public List<VisitDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

        StringBuilder requestString = new StringBuilder("/stats?"
                + "start=" + start.format(formatter)
                + "&end=" + end.format(formatter)
                + "&unique=" + unique);

        for (String uri : uris) {
            requestString.append("&uris=").append(uri);
        }

        ResponseEntity<List<VisitDto>> responseEntity = rest.exchange(
                requestString.toString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<VisitDto>>() {
                }
        );

        return responseEntity.getBody();
    }
}
