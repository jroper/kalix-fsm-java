package com.example.kalixfsm.domain;

import com.example.kalixfsm.fsm.FsmEventSourcedEntity;
import com.example.kalixfsm.fsm.FsmState;
import com.google.protobuf.Empty;
import example.kalixfsm.Shoppingcart;
import example.kalixfsm.domain.ShoppingCartDomain;
import kalix.javasdk.eventsourcedentity.CommandContext;

public class ShoppingCart extends FsmEventSourcedEntity {

  @Override
  public Object emptyState() {
    return ShoppingCartDomain.NotCreated.newBuilder().build();
  }

  {
    // Not created state
    FsmState<ShoppingCartDomain.NotCreated> notCreated = newState(ShoppingCartDomain.NotCreated.class);

    notCreated.registerCommandHandler("Create", this::create);

    notCreated.registerTransition(this::cartCreated);


    // Active state
    FsmState<ShoppingCartDomain.Active> active = newState(ShoppingCartDomain.Active.class);

    active.registerCommandHandler("AddItem", this::addItem);
    active.registerCommandHandler("RemoveItem", this::removeItem);
    active.registerCommandHandler("GetCart", this::getCart);
    active.registerCommandHandler("Checkout", this::checkout);
    active.registerCommandHandler("RemoveCart", this::removeCart);

    active.registerTransition(this::itemAdded);
    active.registerTransition(this::itemRemoved);


    // Checked out state
    FsmState<ShoppingCartDomain.CheckedOut> checkedOut = newState(ShoppingCartDomain.CheckedOut.class);

    // Deleted state
    FsmState<ShoppingCartDomain.Deleted> deleted = newState(ShoppingCartDomain.Deleted.class);

  }

  private Effect<Object> create(CommandContext ctx, ShoppingCartDomain.NotCreated state, Shoppingcart.CreateCart command) {
    return effects()
        .emitEvent(ShoppingCartDomain.CartCreated.newBuilder().setTimestamp(System.currentTimeMillis()).build())
        .thenReply(s -> Shoppingcart.CartCreated.newBuilder().setCartId(ctx.entityId()).build());
  }

  private ShoppingCartDomain.Active cartCreated(ShoppingCartDomain.NotCreated state, ShoppingCartDomain.CartCreated created) {
    return ShoppingCartDomain.Active.newBuilder().setCart(
        Shoppingcart.Cart.newBuilder().setCreationTimestamp(created.getTimestamp())
    ).build();
  }

  private Effect<Empty> addItem(CommandContext ctx, ShoppingCartDomain.Active state, Shoppingcart.AddLineItem command) {
    return effects()
        .emitEvent(ShoppingCartDomain.ItemAdded.newBuilder()
            .setItem(Shoppingcart.LineItem.newBuilder()
                .setName(command.getName())
                .setProductId(command.getProductId())
                .setQuantity(command.getQuantity())
            ).build()).thenNoReply();
  }

  private Effect<Empty> removeItem(CommandContext ctx, ShoppingCartDomain.Active state, Shoppingcart.RemoveLineItem command) {
    for (Shoppingcart.LineItem item : state.getCart().getItemsList()) {
      if (item.getProductId().equals(command.getProductId())) {
        return effects().emitEvent(ShoppingCartDomain.ItemRemoved.newBuilder().setProductId(command.getProductId())).thenNoReply();
      }
    }
    return effects().error("Item " + command.getProductId() + " not found.");
  }

  private Effect<Shoppingcart.Cart> getCart(CommandContext ctx, ShoppingCartDomain.Active state, Shoppingcart.GetShoppingCart command) {
    return effects().reply(state.getCart());
  }

  private Effect<Shoppingcart.CheckoutConfirmation> checkout(CommandContext ctx, ShoppingCartDomain.Active state, Shoppingcart.CheckoutCart command) {
    return effects().emitEvent(ShoppingCartDomain.CartCheckedOut.newBuilder().setConfirmationId("some-confirmation-id").build())
        .thenReply(s -> Shoppingcart.CheckoutConfirmation.newBuilder().setConfirmationId("some-confirmation-id").build());
  }

  private Effect<Empty> removeCart(CommandContext ctx, ShoppingCartDomain.Active state, Empty command) {
    return effects().emitEvent(ShoppingCartDomain.CartDeleted.newBuilder().build()).thenNoReply();
  }

  private ShoppingCartDomain.Active itemAdded(ShoppingCartDomain.Active state, ShoppingCartDomain.ItemAdded event) {
    // See if we already have this item
    for (int i = 0; i < state.getCart().getItemsCount(); i++) {
      Shoppingcart.LineItem item = state.getCart().getItems(i);
      if (item.getProductId().equals(event.getItem().getProductId())) {
        // Add to its quantity
        return state.toBuilder().setCart(state.getCart().toBuilder().setItems(i,
            item.toBuilder().setQuantity(item.getQuantity() + event.getItem().getQuantity())
        )).build();
      }
    }
    return state.toBuilder().setCart(state.getCart().toBuilder().addItems(event.getItem())).build();
  }

  private ShoppingCartDomain.Active itemRemoved(ShoppingCartDomain.Active state, ShoppingCartDomain.ItemRemoved event) {
    for (int i = 0; i < state.getCart().getItemsCount(); i++) {
      if (state.getCart().getItems(i).getProductId().equals(event.getProductId())) {
        return state.toBuilder().setCart(state.getCart().toBuilder().removeItems(i)).build();
      }
    }
    return state;
  }

}
