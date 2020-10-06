package graphql.core;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GraphQLResponse {

  private Map<String, Object> data;
  private List<GraphQLError> errors;
}
