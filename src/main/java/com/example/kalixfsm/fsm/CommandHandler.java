package com.example.kalixfsm.fsm;

import kalix.javasdk.eventsourcedentity.CommandContext;
import kalix.javasdk.eventsourcedentity.EventSourcedEntity;

public interface CommandHandler<S, C, R> {
  EventSourcedEntity.Effect<R> handle(CommandContext context, S state, C command);
}
