package graphql.core;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GraphQLRequest {

  private String query;
  private Object variables;
  private String operationName;

}
