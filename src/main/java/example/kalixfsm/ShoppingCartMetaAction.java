package example.kalixfsm;

import com.google.protobuf.Empty;
import kalix.javasdk.action.ActionCreationContext;

// This class was initially generated based on the .proto definition by Kalix tooling.
// This is the implementation for the Action Service described in your example/kalixfsm/shoppingcart.proto file.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class ShoppingCartMetaAction extends AbstractShoppingCartMetaAction {

  public ShoppingCartMetaAction(ActionCreationContext creationContext) {}

  @Override
  public Effect<Shoppingcart.States> getStates(Empty empty) {
    throw new RuntimeException("The command handler for `GetStates` is not implemented, yet");
  }
}
