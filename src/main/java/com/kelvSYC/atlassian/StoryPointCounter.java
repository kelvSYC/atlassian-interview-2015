package com.kelvSYC.atlassian;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Object tabulating story points per issue type.
 * @author kelvSYC
 *
 */
public class StoryPointCounter {
	private final JiraClient client;
	
	public StoryPointCounter(JiraClient client) {
		this.client = client;
	}
	
	/**
	 * Simple "shortcut" returning a trivial collector collecting map entries into a map.
	 * @return
	 */
	private static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> entriesToMap() {
		return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
	}
	
	/**
	 * Simple "shortcut returning a trivial collector collecting map entries into a concurrent map.
	 * 
	 * Use this instead of {@link #entriesToMap()} in the event that parallel streams need to be used.
	 */
	private static <K, V> Collector<Map.Entry<K, V>, ?, ConcurrentMap<K, V>> entriesToConcurrentMap() {
		return Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue);
	}
	
	/**
	 * Convenience "map method" for transforming the values of map entries while keeping keys intact.
	 */
	private static <K, V1, V2> Function<Map.Entry<K, V1>, Map.Entry<K, V2>> mapValues(Function<? super V1, ? extends V2> valueMapper) {
		return entry -> new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), valueMapper.apply(entry.getValue()));
	}
	
	public Map<String, Integer> getTotalsByIssueType(List<String> issueTypes) {
		// This algorithm is essentially a series of map operations, for which each step is labelled discretely for convenience and testability:
		// 1. Retrieve all issue strings for an issue type by using the client, store in map
		// 2. For each issue type, transform the issue strings to issue IDs
		// 3. For each issue ID, transform to issue estimates by using the client
		// 4. Sum up the estimates.
		
		Map<String, List<String>> issueMap = issueTypes.stream().map(issue -> new AbstractMap.SimpleImmutableEntry<>(issue, client.getIssueType(issue).getIssues())).collect(entriesToMap());
		Map<String, List<Integer>> issueIdMap = issueMap.entrySet().stream().map(mapValues(issues -> issues.stream().map(issue -> issue.substring(8)).map(Integer::valueOf).collect(Collectors.toList()))).collect(entriesToMap());
		Map<String, List<Integer>> issueEstimatesMap = issueIdMap.entrySet().stream().map(mapValues(ids -> ids.stream().map(client::getIssue).map(IssueResponse::getEstimate).map(Integer::valueOf).collect(Collectors.toList()))).collect(entriesToMap());
		Map<String, Integer> result = issueEstimatesMap.entrySet().stream().map(mapValues(estimates -> estimates.stream().mapToInt(Integer::intValue).sum())).collect(entriesToMap());
		
		return result;
	}
}
