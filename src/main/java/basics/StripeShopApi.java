package basics;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import java.util.HashMap;
import java.util.Map;

public class StripeShopApi implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static Gson gson = new Gson();
    private static String TABLE_NAME = "BasicInitOrderTable";
    String id;
    String sessionIdJson;

    Map<String, String> responseData = new HashMap();

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        com.stripe.Stripe.apiKey = "sk_test_51HQynJGNtnHiw0AlpAYEhhiyMhiecjhrpED7XBh9IKXAy3FnmxzGzVzVn7oF9YXInNuFJzzVdr2RgMYuNixeJNVF00cfTLWVfy";
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("https://example.com/success")
                        .setCancelUrl("https://example.com/cancel")
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("usd")
                                                        .setUnitAmount(2000L)
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("T-shirt")
                                                                        .build())
                                                        .build())
                                        .build())
                        .build();

        try {
            Session session = Session.create(params);
            responseData.put("sessionId", session.getId());
        } catch (StripeException e) {
            e.printStackTrace();
        }

        sessionIdJson = gson.toJson(responseData);


        System.out.println(sessionIdJson);



        HashMap<String, String> header = new HashMap<>();
        header.put("Access-Control-Allow-Origin", "*");
        header.put("Access-Control-Allow-Credentials", "true");
        return new APIGatewayProxyResponseEvent()
                .withBody(sessionIdJson)
                .withHeaders(header)
                .withStatusCode(200);

    }


}

