
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://40.68.116.191:8080")
		.inferHtmlResources()
		.acceptHeader("image/webp,image/apng,image/*,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("fr")
		.userAgentHeader("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")

	val headers_0 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_1 = Map("Pragma" -> "no-cache")



	val scn = scenario("RecordedSimulation")
		.exec(http("request_0")
			.get("/greeting")
			.headers(headers_0)
			.resources(http("request_1")
			.get("/favicon.ico")
			.headers(headers_1)))

	setUp(scn.inject(atOnceUsers(500))).protocols(httpProtocol)
}