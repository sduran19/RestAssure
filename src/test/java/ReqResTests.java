import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import prueba_customer.Customer;
import prueba_customer.Identification;
import prueba_customer.PruebaCustomer;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ReqResTests {

    @BeforeEach
    public void setup(){
        RestAssured.baseURI = ConfVariables.getHost();
        RestAssured.basePath = ConfVariables.getPath();
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured());
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    public void loginTest(){
        given()
                .body("{\n" +
                        "    \"email\": \"eve.holt@reqres.in\",\n" +
                        "    \"password\": \"cityslicka\"\n" +
                        "}")
                .post("login")
                .then()
                .statusCode(HttpStatus.SC_OK)
        .body("token",notNullValue());
    }

    @Test
    public void getSingleUserTest(){
        given()
                .get("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("data.id", Matchers.equalTo(2));
    }

    @Test
    public void deleteUserTest(){
        given()
                .delete("users/2")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void patchUserTest(){
        String nameUpdated = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .patch("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().jsonPath().getString("name");

        MatcherAssert.assertThat(nameUpdated,equalTo("morpheus"));
    }

    @Test
    public void putUserTest(){
        String nameUpdated = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .put("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().jsonPath().getString("job");

        MatcherAssert.assertThat(nameUpdated,equalTo("zion resident"));
    }

    @Test
    public void getAllUsersTest(){
        Response response = given()
                .get("users?page=2");

        Headers headers = response.getHeaders();
        int statusCode = response.getStatusCode();
        String body = response.getBody().asString();
        String contentType = response.contentType();

        MatcherAssert.assertThat(statusCode,equalTo(HttpStatus.SC_OK));
        System.out.println("Headers-----------------> " + headers);
        System.out.println("Body-----------------> " + body);
        System.out.println("contentType-----------------> " + contentType);
    }


    @Test
    public void getAllUsersTest2(){
        String response = given().when()
                .get("users?page=2").then().extract().body().asString();

        int page = JsonPath.from(response).get("page");
        int total_pages = JsonPath.from(response).get("total_pages");

        int idFirstUser = JsonPath.from(response).get("data[0].id");

        System.out.println(page);
        System.out.println(total_pages);
        System.out.println(idFirstUser);

        List<Map> userWithIdGreaterThan10 = JsonPath.from(response).get("data.findAll {user -> user.id > 10}");
        String email = userWithIdGreaterThan10.get(0).get("email").toString();
        System.out.println("email del usuario " + email);

        List<Map> user = JsonPath.from(response).get("data.findAll { user -> user.id > 10 && user.last_name == 'Howell'}");
        int id = Integer.parseInt(user.get(0).get("id").toString());
        System.out.println("ID usuario Howell " + id);
    }

    @Test
    public void createUsersTest(){
        String response = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"leader\"\n" +
                        "}")
                .post("users").then().extract().body().asString();

        User user = JsonPath.from(response).getObject("", User.class);
        System.out.println(user.getId());
        System.out.println(user.getJob());
    }

    @Test
    public void getUsersTest(){
        String response = given()
                .when()
                .get("users?page=2").then().extract().body().asString();

         List<GetUsers> users = JsonPath.from(response).getList("data", GetUsers.class);
        System.out.println(users.size());
        for (GetUsers user: users) {
            System.out.println(user.getFirst_name());
        }
    }

    @Test
    public void postCreateUserTest(){

        CreateUserRequest userRequest = new CreateUserRequest();

        userRequest.setEmail("eve.holt@reqres.in");
        userRequest.setPassword("pistol");

        String response = given()
                .when()
                .body(userRequest)
                .post("register").then().extract().body().asString();

        CreateUserResponse users = JsonPath.from(response).getObject("", CreateUserResponse.class);

        System.out.println("ID ---> " + users.getId());
        System.out.println("PASSWORD ---> " + users.getToken());

    }

    @Test
    public void pruebaCustomerTest(){

        Identification userRequest = new Identification();
        Customer customer = new Customer();
        PruebaCustomer pruebaCustomer = new PruebaCustomer();

        userRequest.setType("CC");
        userRequest.setNumber("00011010");

        customer.setIdentification(userRequest);
        pruebaCustomer.setCustomer(customer);

        String response = given()
                .when()
                .body(pruebaCustomer)
                .post("register").then().extract().body().asString();

        CreateUserResponse users = JsonPath.from(response).getObject("", CreateUserResponse.class);

        System.out.println("ID ---> " + users.getId());
        System.out.println("PASSWORD ---> " + users.getToken());

    }

}