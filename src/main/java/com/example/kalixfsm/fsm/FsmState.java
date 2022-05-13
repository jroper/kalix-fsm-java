package com.example.kalixfsm.fsm;

import net.jodah.typetools.TypeResolver;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public final class FsmState<S> {
  private final Map<Class<?>, StateTransitionHandler<S, ?, ?>> stateTransitionHandlers = new HashMap<>();
  private final Map<Class<?>, Class<?>[]> transitions = new HashMap<>();
  private final Map<String, CommandHandler<?, ?, ?>> commandHandlers = new HashMap<>();

  FsmState() {
  }

  public <E, T> void registerTransition(StateTransitionHandler<S, E, T> handler) {
    Class<?>[] args = TypeResolver.resolveRawArguments(StateTransitionHandler.class, handler.getClass());
    Class<?> eventClass = args[1];
    if (stateTransitionHandlers.containsKey(eventClass)) {
      throw new IllegalArgumentException("A state transition handler is already registered on this state for event " + eventClass);
    }
    Class<?> transitionToClass = args[2];
    if (Modifier.isAbstract(transitionToClass.getModifiers()) || transitionToClass.equals(Object.class)) {
      throw new IllegalArgumentException("Cannot infer the state(s) that this transition will transition to, " +
          "please pass a list of possible transition classes on registration.");
    }
    transitions.put(eventClass, new Class<?>[] {transitionToClass});
    stateTransitionHandlers.put(eventClass, handler);
  }

  public <E, T> void registerTransition(StateTransitionHandler<S, E, T> handler, Class<?>... transitionTos) {
    Class<?> eventClass = TypeResolver.resolveRawArguments(StateTransitionHandler.class, handler.getClass())[1];
    if (stateTransitionHandlers.containsKey(eventClass)) {
      throw new IllegalArgumentException("A state transition handler is already registered on this state for event " + eventClass);
    }

    transitions.put(eventClass, transitionTos);
    stateTransitionHandlers.put(eventClass, handler);
  }

  public <C, R> void registerCommandHandler(String name, CommandHandler<S, C, R> handler) {
    // todo validate that name is a valid command name on the service, and that the type C matches its input type, and
    // type R matches its return type
    if (commandHandlers.containsKey(name)) {
      throw new IllegalArgumentException("A command handler is already registered on this state for command " + name);
    }
    commandHandlers.put(name, handler);
  }

  <E> StateTransitionHandler<S, E, ?> stateTransitionHandler(E event) {
    return (StateTransitionHandler) stateTransitionHandlers.get(event.getClass());
  }

  <C, R> CommandHandler<S, C, R> commandHandler(String name) {
    return (CommandHandler) commandHandlers.get(name);
  }
}
