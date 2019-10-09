package wikiAPI;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
 * MultiThreadedAPI is similar to API, but MultiThreadedAPI uses 
 * Threadpools to request at a significantly higher rate.
 * //MVC classification: M
 */


public class MultiThreadedAPI {
	
	public static String[] links(String[] titles, int numThreads) throws InterruptedException{
		return joinedMultiThreadedAPICall(API::links, titles, numThreads);
	}
	
	public static String[] subCategories(String[] titles, int numThreads) throws InterruptedException{
		return joinedMultiThreadedAPICall(API::subCategories, titles, numThreads);
	}
	
	public static String[] superCategories(String[] titles, int numThreads) throws InterruptedException{
		return joinedMultiThreadedAPICall(API::superCategories, titles, numThreads);
	}
	
	public static Map<String, String[]> superCategoriesSeperated(String[] titles, int numThreads) throws InterruptedException{
		return seperatedMultithreadedAPICall(API::superCategories, titles, numThreads);
	}
	
	public static Map<String, String> extractsSeperated(String[] titles, int numThreads) throws InterruptedException{
		return seperatedMultithreadedAPICall(API::extracts, titles, numThreads);
	}
	
	public static String[] fromCategories(String[] titles, int numThreads) throws InterruptedException{
		return joinedMultiThreadedAPICall(API::fromCategory, titles, numThreads);
	}
	
	public static Map<String, String[]> linksSeperated(String[] titles, int numThreads) throws InterruptedException{
		return seperatedMultithreadedAPICall(API::links, titles, numThreads);
	}
	
	private static String[] joinedMultiThreadedAPICall(valueGetter<String[]> get, String[] titles, int numThreads) throws InterruptedException {
		ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
		List<Future<String[]>> futureList = Collections.synchronizedList(new ArrayList<Future<String[]>>());
		
		for (String title : titles) {
			Future<String[]> future = threadPool.submit(()->{
				try {
					return get.get(title);
				} catch (Exception e) {
					e.printStackTrace();
					return new String[0];
				}
			});
			futureList.add(future);
		}
		String[] ret =  (String[])getFromFutureList(futureList);
		threadPool.shutdown();
		return ret;
	}
	
	private static <T> Map<String, T> seperatedMultithreadedAPICall(valueGetter<T> get, String[] titles, int numThreads) throws InterruptedException{
		ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
		List<Future<SimpleEntry<String, T>>> futureList = 
				Collections.synchronizedList(new ArrayList<Future<SimpleEntry<String, T>>>());
			
		for (String title : titles) {
			Future<SimpleEntry<String, T>> future = threadPool.submit(()->{
				return new SimpleEntry(title, get.get(title));	
			});
			futureList.add(future);
		}
		
		Map<String, T> result = new HashMap<String, T>();
		for (Future<SimpleEntry<String, T>> entry : futureList) {
			try {
				SimpleEntry<String, T> temp = entry.get();
				result.put(temp.getKey(), temp.getValue());
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		threadPool.shutdown();
		return result;
	}
	
	private static String[] getFromFutureList(List<Future<String[]>> futureList) throws InterruptedException {
		Set<String> ret = new HashSet<String>();
		for (Future future : futureList) {
			try {
				for (String s:(String[])future.get()) {
					if (s != null) ret.add(s);
				}
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return ret.toArray(new String[0]);
	}
}

@FunctionalInterface
interface valueGetter<T> {
	public T get(String input) throws Exception;
}
