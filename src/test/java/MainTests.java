import org.junit.jupiter.api.*;

public class MainTests {

    @BeforeEach
    public void beforeEachTest(){

    }

    @BeforeAll
    public static void beforeAllTest(){
        System.out.println("Close all");
    }

    @AfterAll
    public static void afterAllTest(){
        System.out.println("Start all");
    }

    @AfterEach
    public void afterEachTest(){

    }

    @Test
    public void add1(){
        //arrange
        String argument = "[{\"id\":3,\"firstName\":\"Petya\",\"lastName\":\"Lavrov\",\"country\":\"USA\",\"age\":10}," +
                "{\"id\":4,\"firstName\":\"Gosha\",\"lastName\":\"Petrov\",\"country\":\"RU\",\"age\":15}]";

        //act

        //assert

    }


}
