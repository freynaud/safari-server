import org.openqa.safari.Utils;
import org.testng.Assert;
import org.testng.annotations.Test;


public class UtilsTest {

	
	
	@Test
	public void extractElement(){
		String path ="/session/f17d2f6c-33bc-46d8-8369-4f1dd24c44d9/element/1/value";
		String s = Utils.extractElementIdFromPath(path);
		Assert.assertEquals(s, "1");
	}
	
	
	
	@Test
	public void genericPathTest(){
		String path ="/session/f17d2f6c-33bc-46d8-8369-4f1dd24c44d9/element/1/value";
		String s = Utils.getGenericPath(path);
		Assert.assertEquals(s, "/session/:sessionId/element/:id/value");
	}
}
