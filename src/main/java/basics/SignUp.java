package basics;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignUp implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion(Regions.US_WEST_1)
            .build();
    private final String TABLE_NAME = "BasicInitOrderTable";


    Map<String, String> jMap = new HashMap<>();
    HashMap<String, AttributeValue> map = new HashMap<>();
    String id;


    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            //parses the json body
            ObjectMapper mapper = new ObjectMapper();
            jMap = mapper.readValue(input.getBody(), Map.class);


        } catch (IOException e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody("NOT OK");
        }
        UUID uuid = new UUID(32, 32).randomUUID();
        id = uuid.toString();
        System.out.println(jMap.get("date"));
        System.out.println(jMap.get("email"));
        System.out.println(jMap.get("phone"));
        System.out.println(jMap.get("name"));

        AttributeValue orderId = new AttributeValue().withS(id);
        AttributeValue name = new AttributeValue().withS(jMap.get("name"));
        AttributeValue phone = new AttributeValue().withS(jMap.get("phone"));
        AttributeValue email = new AttributeValue().withS(jMap.get("email"));
        AttributeValue signUpDates = new AttributeValue().withS(jMap.get("date"));

        map.put("OrderId", orderId);
        map.put("name", name);
        map.put("phone", phone);
        map.put("email", email);
        map.put("ClassSession", signUpDates);

        putRequest(map);
        HashMap<String, String> header = new HashMap<>();
        header.put("Access-Control-Allow-Origin", "*");
        header.put("Access-Control-Allow-Credentials", "true");

        return new APIGatewayProxyResponseEvent()
                .withHeaders(header)
                .withStatusCode(200)
                .withBody("ok");
    }
    private void putRequest(Map<String, AttributeValue> parameters) {

        PutItemRequest request = new PutItemRequest()
                .withTableName(TABLE_NAME)
                .withItem(parameters);
        PutItemResult result = ddb.putItem(request);

    }
}

