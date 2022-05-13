package com.example.kalixfsm.fsm;

import kalix.javasdk.eventsourcedentity.CommandContext;
import kalix.javasdk.eventsourcedentity.EventSourcedEntity;
import kalix.javasdk.impl.eventsourcedentity.EventSourcedEntityRouter;

public final class FsmEventSourcedEntityRouter<E extends FsmEventSourcedEntity> extends EventSourcedEntityRouter<Object, E> {
  private final E entity;
  private Object stateCache;
  private FsmState<?> behaviorCache;

  public FsmEventSourcedEntityRouter(E entity) {
    super(entity);
    this.entity = entity;

    // At this point, we could validate the entity, for example, validate that every command is handled by at least one
  }

  @Override
  public Object handleEvent(Object state, Object event) {
    FsmState<Object> fsmState = getBehavior(state);

    Object newState = fsmState.stateTransitionHandler(event).handle(state, event);
    // Verify that there is a behavior for the given event.
    getBehavior(newState);
    return newState;
  }

  @Override
  public EventSourcedEntity.Effect<?> handleCommand(String commandName, Object state, Object command, CommandContext context) {
    FsmState<Object> fsmState = getBehavior(state);
    CommandHandler<Object, Object, Object> handler = fsmState.commandHandler(commandName);
    if (handler == null) {
      throw new IllegalArgumentException("Command " + commandName + " is not valid for state " + state);
    }
    return handler.handle(context, state, command);
  }

  private <S> FsmState<S> getBehavior(S state) {
    if (state == stateCache) {
      return (FsmState) behaviorCache;
    } else {
      FsmState<S> fsmState = entity.state(state);
      if (fsmState == null) {
        throw new IllegalArgumentException("No behaviour for state " + state);
      }
      stateCache = state;
      behaviorCache = fsmState;
      return fsmState;
    }
  }
}
