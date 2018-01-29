import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
public class TestApplication {
    @Test
    public void countingSort(){
        Application application = new Application();
        List<Integer> values = new ArrayList<>();
        //List<Integer> expected = List<>(asList(1,2,3,4,5,6,7,8,9,10)); //Zeile ändern
        //List<Integer> actual = application.sort(values);  //Zeile Bitte ändern
        Assert.assertEquals(actual, expected);
    }
}
