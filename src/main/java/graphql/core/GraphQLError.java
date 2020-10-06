package graphql.core;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
class GraphQLError {

  private String message;
  private List<GraphQLLocation> locations;
  private List<String> path;
  private Map<String, Object> extensions;
}
