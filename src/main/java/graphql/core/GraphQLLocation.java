package graphql.core;

import lombok.Data;

@Data
public class GraphQLLocation {

  private int line;
  private int column;
}
