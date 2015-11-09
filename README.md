[![Build Status](https://travis-ci.org/haffla/soundcloud-scala.svg)](https://travis-ci.org/haffla/soundcloud-scala) [![Codacy Badge](https://api.codacy.com/project/badge/7a481c68837549f7aaa4f630153369fa)](https://www.codacy.com/app/jakobpupke_2054/soundcloud-scala)

## soundcloud-scala

A friendly, asynchronous wrapper around Soundcloud's API.

### Installation
sbt:

    libraryDependencies += com.github.haffla" %% "soundcloud-scala" % "0.1-SNAPSHOT"

	resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

### Usage
#### Minimal
In case you only need simple access to Soundcloud's API, that is without OAuth2 authorization, the following will be sufficient. You only need a CLIENT_ID (of your app registered on Soundcloud) to instantiate the Client.
 
    import com.github.haffla.soundcloud.Client
    
    // use whatever Json library you prefer
    import play.api.libs.json.Json
    
    val client = Client(%YOUR_CLIENT_ID%)
    val someUser:Future[String] = client.users("6563020")()
    // All methods return a Future[String], i.e. it's JSON.
    
    someUser map {
	    user => 
		    val json = Json.parse(user)
		    val username = (json \ "username").asOpt[String] // -> Some("haffla")
	    }
	
	/* more examples */

	//comments of track with id 13158665
	val commentsOfSomeTrack = client.tracks("13158665")("comments")

	//get list of users followers, limit 2
	client.users("6563020")("followers", 2)

#### Full example with Authentication using Play framework 

	...
	
	import com.github.haffla.soundcloud.Client	

	class MyController extends Controller {
		
	  /* Replace with your own redirect uri. 
	  This is just an example from my development machine */
	  val client = Client(%YOUR_CLIENT_ID%, %YOUR_CLIENT_SECRET%, 
  						  "http://localhost:9000/soundcloud/callback")
                      
      val queryString:Map[String,Seq[String]] = Map(
  	    "response_type" -> Seq("code"),
  	    "client_id" -> Seq(%YOUR_CLIENT_ID%),
  	    "redirect_uri" -> Seq(%YOUR_REDIRECT_URI%),
  	    "scope" -> Seq("non-expiring")
  	  )
	  
	  def login = Action { implicit request =>
	    Redirect("https://api.soundcloud.com/connect", queryString)
	  }
		
	  //This is your callback where Soundcloud redirects you after login
	  def callback = Action.async { implicit request =>
	  
	    /* get the code that Soundcloud is sending you */
	    val code = request.getQueryString("code").orNull //orNull is bad...
	    
	    /* Now use that code to get a real access token from Soundcloud */
	    val authCredentials:Future[String] = client.exchange_token(code)
	    
	    authCredentials map { jsonString =>
		    val json = Json.parse(jsonString)
		    val accessToken = (json \ "access_token").asOpt[String]
			
			/* With this access token you can access Soundcloud's /me endpoint.
			Maybe get the currently logged in user's favourite music? */
			
			client.me(token)() map { user =>
		        val userId = (Json.parse(user) \ "id").as[Int].toString
		        client.users(userId)("favorites") map { favourites =>
			      val usersFavouriteMusic = Json.parse(favourites)
			      
			}
			// do something with this..
	        }
	     }
	  }
	}
