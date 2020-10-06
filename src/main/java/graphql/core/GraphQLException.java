package graphql.core;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Value
@RequiredArgsConstructor
public class GraphQLException extends RuntimeException {

  List<GraphQLError> errors;

  @Override
  public String getMessage() {
    return errors.get(0).getMessage();
  }
}
