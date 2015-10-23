package org.haffla.soundcloud

import org.scalatest._
import org.scalatest.time.{Millis, Span, Seconds}
import org.scalatest.concurrent.ScalaFutures
import net.liftweb.json._

class ClientSpec extends FlatSpec with Matchers with ScalaFutures {

  val client = Client(clientId = sys.env("soundcloud_clientid"),
    clientSecret = sys.env("soundcloud_clientsecret"),
    redirectUri = sys.env("soundcloud_redirecturi"))

  implicit val defaultPatience = PatienceConfig(timeout = Span(2, Seconds), interval = Span(500, Millis))

  "The Client" should "get a user's profile correctly" in {
    val res = client.users("6563020")()
    whenReady(res) { result =>
      val ob = parse(result).values.asInstanceOf[Map[String,Any]]
      ob.get("username") should equal(Some("haffla"))
      ob.get("favorite_dog") should be(None)
      ob.get("city") should be(Some("Berlin"))
    }
  }

  "The Client" should "retrieve and parse a user's sub resource correctly and apply a limit" in {
    val limit = 2
    val res = client.users("6563020")("followers", limit)
    whenReady(res) { result =>
      val ob = parse(result).values.asInstanceOf[List[Map[String,Any]]]
      ob.size should equal(limit)
      ob.head("username") should equal("Christian Kroter")
    }
  }
}
