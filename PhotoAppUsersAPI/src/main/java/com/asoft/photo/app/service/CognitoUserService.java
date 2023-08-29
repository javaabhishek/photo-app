package com.asoft.photo.app.service;

import com.asoft.photo.app.bo.AppClientDetail;
import com.asoft.photo.app.bo.ConfirmUserRequest;
import com.asoft.photo.app.bo.CreateUserRequest;
import com.asoft.photo.app.util.SecretHashUtil;
import com.google.gson.JsonObject;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.List;
import java.util.UUID;

public class CognitoUserService {

    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;

    public CognitoUserService(String region) {
        this.cognitoIdentityProviderClient = CognitoIdentityProviderClient.builder().region(Region.of(region))
                .build();
    }
    public CognitoUserService(CognitoIdentityProviderClient cognitoIdentityProviderClient) {
        this.cognitoIdentityProviderClient = cognitoIdentityProviderClient;
    }

    public JsonObject createUser(CreateUserRequest userRequest){
        JsonObject userDetail=userRequest.getUserDetail();
        AppClientDetail appClientDetail=userRequest.getAppClientDetails();

        String email=userDetail.get("email").getAsString();
        String password=userDetail.get("password").getAsString();
        String userId= UUID.randomUUID().toString();
        String firstName=userDetail.get("firstName").getAsString();
        String lastName=userDetail.get("lastName").getAsString();

        AttributeType emailAttribute= AttributeType.builder()
                .name("email")
                .value(email).build();

        AttributeType nameAttribute= AttributeType.builder()
                .name("name")
                .value(firstName+" "+lastName).build();

        AttributeType userIdAttribute= AttributeType.builder()
                .name("custom:userid")
                .value(userId).build();

        List<AttributeType> lstAttribute=List.of(emailAttribute,nameAttribute,userIdAttribute);

        String secretHash= SecretHashUtil.calculateSecretHash(appClientDetail.getAppClientId()
        ,appClientDetail.getAppClientSecret(),email);

        SignUpRequest signUpRequest=SignUpRequest
                .builder()
                .username(email)
                .password(password)
                .userAttributes(lstAttribute)
                .clientId(appClientDetail.getAppClientId())
                .secretHash(secretHash)
                .build();

        SignUpResponse signUpResponse=cognitoIdentityProviderClient.signUp(signUpRequest);

        JsonObject createUserResult=new JsonObject();
        createUserResult.addProperty("isSuccessful",signUpResponse.sdkHttpResponse().isSuccessful());
        createUserResult.addProperty("statusCode",signUpResponse.sdkHttpResponse().statusCode());
        createUserResult.addProperty("cognitoUserId",signUpResponse.userSub());
        createUserResult.addProperty("isConfirmed",signUpResponse.userConfirmed());

        return createUserResult;
    }

    public JsonObject confirmUser(ConfirmUserRequest confirmUserRequest){
        AppClientDetail appClientDetail=confirmUserRequest.getAppClientDetail();

        String email=confirmUserRequest.getUserDetail().get("email").getAsString();
        String confirmationCode=confirmUserRequest.getConfirmationCode();
        String generatedSecretHash=SecretHashUtil
                .calculateSecretHash(appClientDetail.getAppClientId(),
                        appClientDetail.getAppClientSecret()
                ,email);

        ConfirmSignUpRequest confirmSignUpRequest=ConfirmSignUpRequest.builder()
                .clientId(appClientDetail.getAppClientId())
                .confirmationCode(confirmationCode)
                .secretHash(generatedSecretHash)
                .username(email)
                .build();

        ConfirmSignUpResponse confResponse=cognitoIdentityProviderClient
                .confirmSignUp(confirmSignUpRequest);

        JsonObject confirmUserResponse=new JsonObject();
        confirmUserResponse.addProperty("isSuccessful",confResponse.sdkHttpResponse().isSuccessful());
        confirmUserResponse.addProperty("statusCode",confResponse.sdkHttpResponse().statusCode());

        return confirmUserResponse;
    }
}
