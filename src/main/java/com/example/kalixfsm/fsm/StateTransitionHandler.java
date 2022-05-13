package com.example.kalixfsm.fsm;

public interface StateTransitionHandler<S, E, T> {
  T handle(S state, E event);
}
