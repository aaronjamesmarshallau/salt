import me.i18u.PtvClient
import kotlin.test.Test
import kotlin.test.assertEquals

class TestPtvClient : PtvClient("3002747", "232d2a75-fd51-4507-bbbb-fd1701debd84") {

}

class PtvClientTests {
    @Test
    fun testUrl_Slug_IsInOutput() {
        val testPtvClient = TestPtvClient()
        val url = testPtvClient.getUrl("/v3/route_types", mapOf())

        assertEquals(
            url,
            "https://timetableapi.ptv.vic.gov.au/v3/route_types?devid=3002747&signature=1D311677A54F14DFE407F3DAF550B856DF99F1C3",
            "Calculated string was incorrect"
        )
    }
}