package graphql.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.configuration.GraphQLConfiguration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class GraphQLClient {

  private final GraphQLConfiguration graphQLConfiguration;
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  @PostConstruct
  public void init() {

    assert restTemplate != null;

  }

  private String loadResource(Resource resource) throws IOException {
    try (InputStream inputStream = resource.getInputStream()) {
      return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
    }
  }

  private String load(String queryPath) throws IOException {
    return loadResource(new ClassPathResource(queryPath));
  }

  public <T> T execute(String resource, Map<String, Object> variables, Class<T> retClass) throws IOException {
    return this.execute(resource, variables, retClass, null);
  }

  public <T> T execute(String resource, Map<String, Object> variables, Class<T> retClass, String accessToken) throws IOException {
    GraphQLRequest request = buildGraphQLRequest(resource, variables);
    HttpEntity<GraphQLRequest> entity = buildHttpEntity(request, accessToken);
    ResponseEntity<GraphQLResponse> response = getGraphQLResponse(entity);

    if (response.getBody().getData() != null) {
      Map<String, Object> data = response.getBody().getData();
      return data.values().size() == 1 ?
          objectMapper.convertValue(response.getBody().getData().values().toArray()[0], retClass) :
          objectMapper.convertValue(response.getBody().getData(), retClass);
    }
    return null;
  }

  private HttpEntity<GraphQLRequest> buildHttpEntity(GraphQLRequest graphQLRequest, String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (accessToken != null) {
      headers.set(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken));
    }
    return new HttpEntity<>(graphQLRequest, headers);
  }

  private GraphQLRequest buildGraphQLRequest(String resource, Map<String, Object> variables) throws IOException {
    return GraphQLRequest.builder().query(load(resource)).variables(variables).build();
  }

  private ResponseEntity<GraphQLResponse> getGraphQLResponse(HttpEntity<GraphQLRequest> entity) {
    log.info("Executing query: " + graphQLConfiguration.getUrl());
    log.info(entity.toString());
    return restTemplate.postForEntity(graphQLConfiguration.getUrl(), entity, GraphQLResponse.class);
  }

}

