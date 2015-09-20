package com.kelvSYC.atlassian;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of {@link JiraClient} based around a rudimentary HTTP client.
 * @author kelvSYC
 *
 */
public class JiraClientImpl implements JiraClient {
	private final HttpClient client;
	private final String endpoint;
	
	private transient final ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * Simple {@link ResponseHandler} implementation that deserializes JSON into the desired object type.
	 * @author kelvSYC
	 *
	 * @param <T>	Value type
	 */
	private static class JsonResponseHandler<T> implements ResponseHandler<T> {
		private final Class<T> valueClass;
		private final ObjectMapper mapper;
		
		public JsonResponseHandler(Class<T> valueClass, ObjectMapper mapper) {
			this.valueClass = valueClass;
			this.mapper = mapper;
		}
		
		@Override
		public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			InputStream in = response.getEntity().getContent();
			return mapper.readValue(in, valueClass);
		}
	}
	
	public JiraClientImpl(HttpClient client, String endpoint) {
		this.client = client;
		this.endpoint = endpoint;
	}
	
	@Override
	public IssueTypeResponse getIssueType(String issueType) {
		HttpGet request = new HttpGet(endpoint + "/issueTypes/" + issueType);
		try {
			return client.execute(request, new JsonResponseHandler<>(IssueTypeResponse.class, mapper));
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public IssueResponse getIssue(int id) {
		HttpGet request = new HttpGet(endpoint + "/issues/" + id);
		
		try {
			return client.execute(request, new JsonResponseHandler<>(IssueResponse.class, mapper));
		} catch (IOException e) {
			return null;
		}
	}
}
