package com.kelvSYC.atlassian;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Main entry point into the program
 * @author kelvSYC
 *
 */
public class Main {
	public static void printStoryCounts(String apiRoot, List<String> issueTypes) {
		HttpClient baseClient = HttpClients.createDefault();
		JiraClient client = new JiraClientImpl(baseClient, apiRoot);
		StoryPointCounter counter = new StoryPointCounter(client);
		
		Map<String, Integer> counts = counter.getTotalsByIssueType(issueTypes);
		counts.forEach((key, value) -> {
			System.out.printf("%s: %d%n", key, value);
		});
	}
	
	/**
	 * Main entry point.
	 * @param args	Command-line arguments.  First argument is the API root, all others are issue types.
	 */
	public static void main(String... args) {
		String apiRoot = args[0];
		List<String> issueTypes = IntStream.range(1, args.length).mapToObj(i -> args[i]).collect(Collectors.toList());
		printStoryCounts(apiRoot, issueTypes);
	}
}
