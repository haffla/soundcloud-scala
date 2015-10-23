package org.haffla.soundcloud

import dispatch.Defaults._
import dispatch._
import net.liftweb.json._


object Client {
  def apply(clientId:String) = new Client(clientId)
  def apply(clientId:String, clientSecret:String, redirectUri:String) = new Client(clientId, Some(clientSecret), Some(redirectUri))
}

class Client(clientId:String, clientSecret:Option[String] = None, redirectUri:Option[String] = None, host:String = "http://api.soundcloud.com") {

  type anyMap = Map[String,Any]
  
  val authParameters = (clientSecret, redirectUri) match {
    case (Some(secret), Some(uri)) =>
      Some(
        Map(
        "grant_type" -> "authorization_code",
        "redirect_uri" -> uri,
        "client_id" -> clientId,
        "client_secret" -> secret
        )
      )
    case _ => None
  }

  def users(id:String)(subResource:String = "", limit:Int = -1) = {
    doRequest("users", id)(subResource, limit)
  }

  def tracks(id:String)(subResource:String = "", limit:Int = -1) = {
    doRequest("tracks", id)(subResource, limit)
  }

  def authorizeUrl():String = {
    redirectUri match {
      case Some(uri) => uri
      case None => throw new Exception("You've initialized the client without redirect_uri")
    }
  }

  def exchange_token(code:String):Future[Map[String,String]] = {
    val params = authParameters match {
      case None =>
        throw new Exception(
          "In order to authenticate against Soundcloud you have to provide your client_secret and a redirect_uri")
      case Some(parameters) =>
        parameters + ("code" -> code)
    }
    val request = url(host + "/oauth2/token") << params
    val response = Http(request OK as.String)
    for (r <- response)
      yield parse(r).values.asInstanceOf[Map[String,String]]
  }

  private def doRequest(resource:String, id:String)(subResource:String, limit:Int) = {
    val withoutLimit = host + s"/$resource/$id/$subResource?client_id=$clientId"
    val urL = limit match {
      case -1 => withoutLimit
      case _ => withoutLimit + s"&limit=$limit"
    }
    val request = url(urL)
    val response = Http(request OK as.String)
    for (r <- response)
      yield {
        parse(r).values
      }
  }
}