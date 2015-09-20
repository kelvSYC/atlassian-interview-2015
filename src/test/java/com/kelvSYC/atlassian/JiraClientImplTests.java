package com.kelvSYC.atlassian;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Test;

/**
 * Tests relating to {@link JiraClientImpl}.
 * @author kelvSYC
 *
 */
public class JiraClientImplTests {
	@Test
	public void simpleGetIssueType() {
		HttpClient baseClient = mock(HttpClient.class);
		try {
			when(baseClient.execute(any(HttpGet.class), any(ResponseHandler.class))).thenAnswer(invocation -> {
				HttpGet request = invocation.getArgumentAt(0, HttpGet.class);
				ResponseHandler<?> handler = invocation.getArgumentAt(1, ResponseHandler.class);
				
				assertThat("Endpoint URL not in request", request.getURI().toString(), startsWith("http://www.example.com"));
				
				// Remember we are mocking an interface - let's exercise the response handler here
				String json = "{ \"id\": \"/issueTypes/bug\", \"name\": \"bug\", \"issues\": [\"/issues/1\"] }";	// Plausible-looking
				HttpResponse response = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
				response.setEntity(new StringEntity(json));
				return handler.handleResponse(response);
			});
		} catch (Exception e) {
			fail("Error setting up mock");
		}
		
		JiraClientImpl client = new JiraClientImpl(baseClient, "http://www.example.com");
		IssueTypeResponse response = client.getIssueType("bug");
		
		assertEquals("ID mismatch", "/issueTypes/bug", response.getId());
		assertEquals("Name mismatch", "bug", response.getName());
		assertThat("Issues size mismatch", response.getIssues(), hasSize(1));
		assertEquals("Issue mismatch", "/issues/1", response.getIssues().get(0));
	}
	
	@Test
	public void simpleGetIssue() {
		HttpClient baseClient = mock(HttpClient.class);
		try {
			when(baseClient.execute(any(HttpGet.class), any(ResponseHandler.class))).thenAnswer(invocation -> {
				HttpGet request = invocation.getArgumentAt(0, HttpGet.class);
				ResponseHandler<?> handler = invocation.getArgumentAt(1, ResponseHandler.class);
				
				assertThat("Endpoint URL not in request", request.getURI().toString(), startsWith("http://www.example.com"));
				
				// Remember we are mocking an interface - let's exercise the response handler here
				String json = "{ \"id\": \"/issues/1\", \"issuetype\": \"/issuetypes/bug\", \"description\": \"Issue #1\", \"estimate\": \"1\" }";	// Plausible-looking
				HttpResponse response = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
				response.setEntity(new StringEntity(json));
				return handler.handleResponse(response);
			});
		} catch (Exception e) {
			fail("Error setting up mock");
		}
		
		JiraClientImpl client = new JiraClientImpl(baseClient, "http://www.example.com");
		IssueResponse response = client.getIssue(1);
		
		assertEquals("ID mismatch", "/issues/1", response.getId());
		assertEquals("Issue Type mismatch", "/issuetypes/bug", response.getIssueType());
		assertEquals("Description mismatch", "Issue #1", response.getDescription());
		assertEquals("Estimate mismatch", "1", response.getEstimate());
	}
}
