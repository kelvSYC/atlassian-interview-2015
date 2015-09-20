package com.kelvSYC.atlassian;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.Test;

/**
 * Tests relating to {@link StoryPointCounter}.
 * @author kelvSYC
 *
 */
public class StoryPointCounterTests {
	@Test
	public void noIssues() {
		IssueTypeResponse response = new IssueTypeResponse();
		response.setIssues(emptyList());
		JiraClient client = mock(JiraClient.class);
		when(client.getIssueType("bug")).thenReturn(response);
		
		StoryPointCounter counter = new StoryPointCounter(client);
		Map<String, Integer> result = counter.getTotalsByIssueType(singletonList("bug"));
		
		assertThat("Input key not in output map", result, hasKey("bug"));
		assertEquals("Nonzero estimate", 0, result.get("bug").intValue());	// Ambiguous unbox here
	}
	
	@Test
	public void oneIssue() {
		IssueResponse issue = new IssueResponse();
		issue.setEstimate("1");
		IssueTypeResponse response = new IssueTypeResponse();
		response.setIssues(singletonList("/issues/1"));
		JiraClient client = mock(JiraClient.class);
		when(client.getIssueType("bug")).thenReturn(response);
		when(client.getIssue(1)).thenReturn(issue);
		
		StoryPointCounter counter = new StoryPointCounter(client);
		Map<String, Integer> result = counter.getTotalsByIssueType(singletonList("bug"));
		
		assertEquals("Estimate mismatch", 1, result.get("bug").intValue());	// Ambiguous unbox here
	}
	
	@Test
	public void multiIssues() {
		IssueResponse issue = new IssueResponse();
		issue.setEstimate("1");
		IssueResponse issue2 = new IssueResponse();
		issue2.setEstimate("2");
		IssueTypeResponse response = new IssueTypeResponse();
		response.setIssues(asList("/issues/1", "/issues/2"));
		JiraClient client = mock(JiraClient.class);
		when(client.getIssueType("bug")).thenReturn(response);
		when(client.getIssue(1)).thenReturn(issue);
		when(client.getIssue(2)).thenReturn(issue2);
		
		StoryPointCounter counter = new StoryPointCounter(client);
		Map<String, Integer> result = counter.getTotalsByIssueType(singletonList("bug"));
		
		assertEquals("Estimate mismatch", 3, result.get("bug").intValue());	// Ambiguous unbox here
	}
	
	@Test
	public void multiIssueTypes() {
		IssueResponse issue = new IssueResponse();
		issue.setEstimate("1");
		IssueResponse issue2 = new IssueResponse();
		issue2.setEstimate("2");
		IssueTypeResponse response = new IssueTypeResponse();
		response.setIssues(singletonList("/issues/1"));
		IssueTypeResponse response2 = new IssueTypeResponse();
		response2.setIssues(singletonList("/issues/2"));
		JiraClient client = mock(JiraClient.class);
		when(client.getIssueType("bug")).thenReturn(response);
		when(client.getIssueType("story")).thenReturn(response2);
		when(client.getIssue(1)).thenReturn(issue);
		when(client.getIssue(2)).thenReturn(issue2);
		
		StoryPointCounter counter = new StoryPointCounter(client);
		Map<String, Integer> result = counter.getTotalsByIssueType(asList("bug", "story"));

		assertEquals("Bug estimate mismatch", 1, result.get("bug").intValue());	// Ambiguous unbox here
		assertEquals("Story estimate mismatch", 2, result.get("story").intValue());
	}
}
