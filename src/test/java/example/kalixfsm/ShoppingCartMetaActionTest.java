package example.kalixfsm;

import akka.stream.javadsl.Source;
import com.google.protobuf.Empty;
import example.kalixfsm.ShoppingCartMetaAction;
import example.kalixfsm.ShoppingCartMetaActionTestKit;
import example.kalixfsm.Shoppingcart;
import kalix.javasdk.testkit.ActionResult;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class ShoppingCartMetaActionTest {

  @Test
  @Ignore("to be implemented")
  public void exampleTest() {
    ShoppingCartMetaActionTestKit service = ShoppingCartMetaActionTestKit.of(ShoppingCartMetaAction::new);
    // // use the testkit to execute a command
    // SomeCommand command = SomeCommand.newBuilder()...build();
    // ActionResult<SomeResponse> result = service.someOperation(command);
    // // verify the reply
    // SomeReply reply = result.getReply();
    // assertEquals(expectedReply, reply);
  }

  @Test
  @Ignore("to be implemented")
  public void getStatesTest() {
    ShoppingCartMetaActionTestKit testKit = ShoppingCartMetaActionTestKit.of(ShoppingCartMetaAction::new);
    // ActionResult<Shoppingcart.States> result = testKit.getStates(Empty.newBuilder()...build());
  }

}
