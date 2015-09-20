package com.kelvSYC.atlassian;

/**
 * Client interface for JIRA's public API.
 * @author kelvSYC
 */
public interface JiraClient {
	IssueTypeResponse getIssueType(String issueType);
	IssueResponse getIssue(int id);
}
