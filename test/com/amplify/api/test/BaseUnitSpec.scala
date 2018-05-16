package com.amplify.api.test

import org.mockito.Mockito.{RETURNS_SMART_NULLS, withSettings}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import scala.reflect.ClassTag

trait BaseUnitSpec
  extends WordSpec
    with MustMatchers
    with MockitoSugar
    with FutureAwaits
    with DefaultAwaitTimeout {

  def strictMock[T <: AnyRef](implicit classTag: ClassTag[T]): T = {
    mock[T](withSettings().defaultAnswer(RETURNS_SMART_NULLS))
  }
}
