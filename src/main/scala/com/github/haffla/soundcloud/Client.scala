package com.github.haffla.soundcloud

import dispatch.Defaults._
import dispatch._

object Client {
  def apply(clientId:String) = new Client(clientId)
  def apply(clientId:String, clientSecret:String, redirectUri:String) = new Client(clientId, Some(clientSecret), Some(redirectUri))
}

class Client(clientId:String, clientSecret:Option[String] = None, redirectUri:Option[String] = None, host:String = "http://api.soundcloud.com") {
  
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

  def users(id:String)(subResource:String = "", limit:Int = -1): Future[String] = {
    request("users", id)(subResource, limit)
  }

  def tracks(id:String)(subResource:String = "", limit:Int = -1): Future[String] = {
    request("tracks", id)(subResource, limit)
  }

  def me(oauthToken:String)(resource:String = "", limit:Int = -1): Future[String] = {
    meRequest(resource, oauthToken, limit)
  }

  def exchange_token(code:String):Future[String] = {
    val params = authParameters match {
      case None =>
        throw new Exception(
          "In order to authenticate against Soundcloud you have to provide your client_secret and a redirect_uri")
      case Some(parameters) =>
        parameters + ("code" -> code)
    }
    val request = url(host + "/oauth2/token") << params
    Http(request OK as.String)
  }

  private def request(resource:String, id:String)(subResource:String, limit:Int): Future[String] = {
    val withoutLimit = host + s"/$resource/$id/$subResource?client_id=$clientId"
    val urL = addLimit(withoutLimit, limit)
    val request = url(urL)
    Http(request OK as.String)
  }

  private def meRequest(resource: String, oauthToken: String, limit:Int): Future[String] = {
    val withoutLimit = host + s"/me/$resource?oauth_token=$oauthToken"
    val urL = addLimit(withoutLimit, limit)
    val request = url(urL)
    Http(request OK as.String)
  }

  // Helper ---------------------

  private def addLimit(url:String, limit:Int):String = {
    limit match {
      case -1 => url
      case _ => url + s"&limit=$limit"
    }
  }

  // Getter ---------------------

  def authorizeUrl():String = {
    redirectUri match {
      case Some(uri) => uri
      case None => throw new Exception("You've initialized the client without redirect_uri")
    }
  }
}