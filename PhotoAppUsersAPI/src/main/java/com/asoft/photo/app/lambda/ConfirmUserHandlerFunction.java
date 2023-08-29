package com.asoft.photo.app.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.asoft.photo.app.bo.AppClientDetail;
import com.asoft.photo.app.bo.ConfirmUserRequest;
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

public class ConfirmUserHandlerFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    Logger log = LogManager.getLogger(CreateUserHandlerFunction.class);

    private final CognitoUserService cognitoUserService;
    private final AppClientDetail appClientDetail;
    public ConfirmUserHandlerFunction(){
        this.cognitoUserService=new CognitoUserService(System.getenv("AWS_REGION"));
        this.appClientDetail=new AppClientDetail();
        appClientDetail.setAppClientId(System.getenv("MY_COGNITO_USER_CLIENT_ID"));
        appClientDetail.setAppClientSecret(System.getenv("MY_COGNITO_USER_CLIENT_SECRET"));
    }
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        Map<String,String> headers=new HashMap<>();
        headers.put("Content-Type","application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);

        try{

            String requestBody=input.getBody();
            log.info("Original JSON body:"+requestBody);
            JsonObject requestJson= JsonParser.parseString(requestBody).getAsJsonObject();
            ConfirmUserRequest confirmUserRequest=new ConfirmUserRequest();
            confirmUserRequest.setUserDetail(requestJson);
            confirmUserRequest.setConfirmationCode(requestJson.get("confimationCode").getAsString());
            confirmUserRequest.setAppClientDetail(appClientDetail);
            //write a java code to create and sign-up user

            JsonObject confirmedResult=cognitoUserService.confirmUser(confirmUserRequest);
            response.withBody(new Gson().toJson(confirmedResult,JsonObject.class))
                    .withStatusCode(200);


        }catch (AwsServiceException ex){
            log.error("Error from Handler:"+ex.awsErrorDetails().errorMessage());
            response.withStatusCode(500);
            response.withBody("Exception:"+ex.awsErrorDetails().errorMessage());
        }catch (Exception ex){
            log.error("Error from Handler:"+ex);
            response.withStatusCode(500);
            response.withBody("Exception:"+ex.getMessage());
        }
        return response;
    }
}
