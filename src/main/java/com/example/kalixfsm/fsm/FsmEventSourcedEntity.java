package com.example.kalixfsm.fsm;

import kalix.javasdk.eventsourcedentity.EventSourcedEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FsmEventSourcedEntity extends EventSourcedEntity<Object> {

  private final Map<Class<?>, FsmState<?>> states = new HashMap<>();

  protected <S> FsmState<S> newState(Class<S> stateClass) {
    if (states.containsKey(stateClass)) {
      throw new IllegalArgumentException("State already registered for class " + stateClass);
    }
    FsmState<S> state = new FsmState<>();
    states.put(stateClass, state);
    return state;
  }

  <S> FsmState<S> state(S state) {
    return (FsmState) states.get(state.getClass());
  }
}
