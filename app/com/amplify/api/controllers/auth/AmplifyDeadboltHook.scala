package com.amplify.api.controllers.auth

import be.objectify.deadbolt.scala.cache.HandlerCache
import play.api.{Configuration, Environment}
import play.api.inject.{Binding, Module}

class AmplifyDeadboltHook extends Module {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(bind[HandlerCache].to[AmplifyHandlerCache])
  }
}
