package com.asoft.photo.app.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.asoft.photo.app.bo.AppClientDetail;
import com.asoft.photo.app.bo.CreateUserRequest;
import com.asoft.photo.app.service.CognitoUserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

import java.util.HashMap;
import java.util.Map;

public class CreateUserHandlerFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    Logger log = LogManager.getLogger(CreateUserHandlerFunction.class);

    private final CognitoUserService cognitoUserService;
    private final AppClientDetail appClientDetail;
    public CreateUserHandlerFunction(){
        this.cognitoUserService=new CognitoUserService(System.getenv("AWS_REGION"));
        this.appClientDetail=new AppClientDetail();
        appClientDetail.setAppClientId(System.getenv("MY_COGNITO_USER_CLIENT_ID"));
        appClientDetail.setAppClientSecret(System.getenv("MY_COGNITO_USER_CLIENT_SECRET"));
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String,String> headers=new HashMap<>();
        headers.put("Content-Type","application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);

        try{
            String requestBody=input.getBody();
            log.info("Original JSON body:"+requestBody);
            JsonObject userDetails= JsonParser.parseString(requestBody).getAsJsonObject();
            CreateUserRequest createUserRequest=new CreateUserRequest();
            createUserRequest.setUserDetail(userDetails);
            createUserRequest.setAppClientDetails(appClientDetail);
            //write a java code to create and sign-up user

            JsonObject createdUserResult=cognitoUserService.createUser(createUserRequest);
            response.withBody(new Gson().toJson(createdUserResult,JsonObject.class))
                    .withStatusCode(200);
        }catch (AwsServiceException ex){
            log.error("Error from Handler:"+ex);
            response.withStatusCode(500);
            response.withBody("Exception:"+ex.getMessage());
        }catch (Exception ex){
            log.error("Error from Handler:"+ex);
            response.withStatusCode(500);
            response.withBody("Exception:"+ex.getMessage());
        }
        return response;
    }

}