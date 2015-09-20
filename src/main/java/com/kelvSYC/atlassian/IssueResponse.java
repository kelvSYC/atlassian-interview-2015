package com.kelvSYC.atlassian;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response object for the Issues API.
 * @author kelvSYC
 *
 */
public class IssueResponse {
	private String id;
	private String issueType;
	private String description;
	private String estimate;
	
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	
	@JsonProperty("issuetype") public String getIssueType() { return issueType; }
	public void setIssueType(String issueType) { this.issueType = issueType; }
	
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	public String getEstimate() { return estimate; }
	public void setEstimate(String estimate) { this.estimate = estimate; }
}
