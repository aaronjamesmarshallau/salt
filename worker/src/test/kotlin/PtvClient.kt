import me.i18u.PtvClient
import kotlin.test.Test
import kotlin.test.assertEquals

class TestPtvClient: PtvClient() {
    
}

class PtvClientTests {
    @Test
    fun testUrl_Slug_IsInOutput() {
        val testPtvClient = TestPtvClient()
        val url = testPtvClient.getUrl("/v3/route_types", mapOf())
        
        assertEquals(url, "https://timetableapi.ptv.vic.gov.au/v3/route_types?devid=example&signature=1b09faabcbe7d88e359691c44305ca93ad314a79", "Calculated string was incorrect")
    }
}