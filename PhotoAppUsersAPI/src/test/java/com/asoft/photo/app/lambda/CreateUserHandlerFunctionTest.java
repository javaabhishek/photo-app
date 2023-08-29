package com.asoft.photo.app.lambda;

import com.amazonaws.xray.AWSXRay;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CreateUserHandlerFunctionTest {

  @Before
  public void setup() {
    if(null == System.getenv("LAMBDA_TASK_ROOT")) {
      AWSXRay.beginSegment("test");
    }
  }

  @After
  public void tearDown() {
    if (AWSXRay.getCurrentSubsegmentOptional().isPresent()) {
      AWSXRay.endSubsegment();
    }

    if(null == System.getenv("LAMBDA_TASK_ROOT")) {
      AWSXRay.endSegment();
    }
  }

  @Test
  public void successfulResponse() {
    /*CreateUserHandlerFunction app = new CreateUserHandlerFunction();
    APIGatewayProxyResponseEvent result = app.handleRequest(null, null);
    assertEquals(result.getStatusCode().intValue(), 200);
    assertEquals(result.getHeaders().get("Content-Type"), "application/json");
    String content = result.getBody();
    assertNotNull(content);
    assertTrue(content.contains("\"message\""));
    assertTrue(content.contains("\"hello world\""));
    assertTrue(content.contains("\"location\""));*/
  }
}