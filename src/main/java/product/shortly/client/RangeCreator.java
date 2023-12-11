package product.shortly.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RangeCreator {
	private final Object lock;
	Logger logger = LoggerFactory.getLogger(RangeCreator.class);
	private final URLRangeClient urlRangeClient;
	private Queue<String> queue;

	public RangeCreator(URLRangeClient urlRangeClient) {
		this.urlRangeClient = urlRangeClient;
		lock = new Object();
		queue = getRange();
	}

	public Queue<String> getRange() {
		// TODO two threads come and check if q is not null and return q .
		//but q has just one element remaining
		if (queue == null || queue.isEmpty()) {
			synchronized (lock) {
				try {
					logger.info("Getting new range");
					if (queue != null && !queue.isEmpty()) {
						logger.info("Was getting new range but another thread has already built the new range");
					} else {
						Map<String, String> map = new HashMap<>();
						ObjectMapper mapper = new ObjectMapper();
						String hashRangeJson = urlRangeClient.getHashRange("hol");
						map = mapper.readValue(hashRangeJson, Map.class);
						String[] list = map.get("range_list").split(",");
						List<String> asList = Arrays.asList(list);
						queue = new ConcurrentLinkedQueue<>(asList);
					}
				} catch (JsonProcessingException e) {
					logger.error("Json Processing exception: Cant fetch a new set of range", e);
				} catch (Exception e) {
					logger.error("UnKnown exception:  Cant fetch a new set of range", e);
				} finally {
					testQueue();
				}
			}
		}

		return queue;
	}

	public void testQueue() {
		if (queue == null || queue.isEmpty()) {
			queue = queue == null ?  new ConcurrentLinkedQueue<>(): queue;
			String prefix = "aaaaaa";
			
			for (char st = 'a'; st <='z' ; st++) {
				queue.add(prefix +"" +st);
			}
		}
	}

	public Queue<String> getQueue() {
		return queue;
	}
}