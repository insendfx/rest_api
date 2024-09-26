import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class DemowebshopTests {
    @BeforeAll
    static void setUp () {
        Configuration.baseUrl = "https://demowebshop.tricentis.com";
        RestAssured.baseURI = "https://demowebshop.tricentis.com";
    }

    @Test
    void addToCartTest() {

        String body = "product_attribute_72_5_18=65&product_attribute_72_6_19=91&product_attribute_72_3_20=58&product_attribute_72_8_30=93&product_attribute_72_8_30=94&product_attribute_72_8_30=95&addtocart_72.EnteredQuantity=1";
        String cookie = "ARRAffinity=215729d76f6e9346a3ca0af641517499b9bd27d0362c4cf136fc5677103585ca; ARRAffinitySameSite=215729d76f6e9346a3ca0af641517499b9bd27d0362c4cf136fc5677103585ca; __utma=78382081.805918759.1727342892.1727342892.1727342892.1; __utmc=78382081; __utmz=78382081.1727342892.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); NopCommerce.RecentlyViewedProducts=RecentlyViewedProductIds=72&RecentlyViewedProductIds=31; __utmt=1; __utmb=78382081.14.10.1727342892";

     given()
             .contentType("application/x-www-form-urlencoded; charset=UTF-8")
             .cookie(cookie)
             .body(body)
             .log().all()
             .when()
             .post("/addproducttocart/details/72/1")
             .then()
             .log().all()
             .statusCode(200)
             .body("success", is(true))
             .body("message", is("The product has been added to your <a href=\"/cart\">shopping cart</a>"));
    }

    @Test
    void addToCartAsAutorizedTest() {

        String body = "product_attribute_72_5_18=65&product_attribute_72_6_19=91&product_attribute_72_3_20=58&product_attribute_72_8_30=93&product_attribute_72_8_30=94&product_attribute_72_8_30=95&addtocart_72.EnteredQuantity=1";
//
        String authCookieName = "NOPCOMMERCE.AUTH";
        String authCookieValue = given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("Email", "profi@mail.ru")
                .formParam("Password","profi123")
                .log().all()
                .when()
                .post("/login")
                .then()
                .log().all()
                .statusCode(302)
                .extract()
                .cookie("NOPCOMMERCE.AUTH");


        String cartSize = given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .cookie(authCookieName, authCookieValue)
                .body(body)
                .log().all()
                .when()
                .post("/addproducttocart/details/72/1")
                .then()
                .log().all()
                .statusCode(200)
                .body("success", is(true))
                .body("message", is("The product has been added to your <a href=\"/cart\">shopping cart</a>"))
                .extract()
                .path("updatetopcartsectionhtml");

        open("/Themes/DefaultClean/Content/images/logo.png");

        Cookie authCookie = new Cookie(authCookieName, authCookieValue);
        WebDriverRunner.getWebDriver().manage().addCookie(authCookie);

        open("");
        $(".cart-qty").shouldHave(text(cartSize));
    }
}
