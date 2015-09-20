package com.kelvSYC.atlassian;

import java.util.List;

/**
 * Response object for the IssueTypes API.
 * @author kelvSYC
 *
 */
public class IssueTypeResponse {
	private String id;
	private String name;
	private List<String> issues;
	
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public List<String> getIssues() { return issues; }
	public void setIssues(List<String> issues) { this.issues = issues; }
}
