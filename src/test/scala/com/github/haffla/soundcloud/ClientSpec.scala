package com.github.haffla.soundcloud

import org.scalatest._
import org.scalatest.time.{Millis, Span, Seconds}
import org.scalatest.concurrent.ScalaFutures
import net.liftweb.json._
import play.api.libs.json.{JsValue, Json}

class ClientSpec extends FlatSpec with Matchers with ScalaFutures {

  val client = Client(clientId = sys.env("soundcloud_clientid"),
    clientSecret = sys.env("soundcloud_clientsecret"),
    redirectUri = sys.env("soundcloud_redirecturi"))

  implicit val defaultPatience = PatienceConfig(timeout = Span(2, Seconds), interval = Span(500, Millis))

  "The Client" should "get a user's profile correctly" in {
    val res = client.users("6563020")()
    whenReady(res) { result =>
      val js = Json.parse(result)
      (js \ "username").as[String] should equal("haffla")
      (js \ "favorite_dog").asOpt[String] should be(None)
      (js \ "city").as[String] should equal("Berlin")
    }
  }

  "The Client" should "retrieve and parse a user's sub resource correctly and apply a limit" in {
    val limit = 2
    val res = client.users("6563020")("followers", limit)
    whenReady(res) { result =>
      val js = Json.parse(result)
      val jsList = (js \ "collection").as[List[JsValue]]
      jsList.size should equal(limit)
      (jsList.head \ "username").as[String] should equal("Christian Kroter")
    }
  }
}
