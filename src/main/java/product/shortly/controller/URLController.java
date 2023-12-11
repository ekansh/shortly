package product.shortly.controller;

import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import product.shortly.client.RangeCreator;
import product.shortly.entity.ShortUrl;
import product.shortly.entity.ShortUrlRepository;
import product.shortly.utils.BasicUtils;

@RestController
public class URLController {
	private final RangeCreator rangeCreator;
	@Autowired
	private ShortUrlRepository repo;
	URLController(RangeCreator rangeCreator){
		this.rangeCreator = rangeCreator;
	}

	Logger logger = LoggerFactory.getLogger(URLController.class);

	@PostMapping(value = "/short", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String,String>> shortenUrl(@RequestBody String input) {
		logger.info("POST:shortenURL");
		String shortUrl = BasicUtils.getEnvVariable().get("shortURL");
		Map<String,String> mapOutput = new HashMap<>();
		ResponseEntity<Map<String,String>> resp = new ResponseEntity<>(mapOutput,  HttpStatus.OK);
		ObjectMapper mapper = new ObjectMapper();
		String url="", shortUrlPrefix ="";
		try {
			Map<String, String> map = mapper.readValue(input, Map.class);
			url = map.get("url");
			shortUrlPrefix = rangeCreator.getRange().poll();
			Timestamp now = Timestamp.from(Instant.now());
			ShortUrl obj = new ShortUrl(shortUrlPrefix,url,now,now); 
			obj.setShortUrl(shortUrlPrefix);
			obj.setLongUrl(url);
			mapOutput.put("shortly", shortUrl + shortUrlPrefix);
			mapOutput.put("originalURL", url);
			repo.saveShortUrl(obj);
		} catch (JsonProcessingException e) {
			mapOutput.clear();
			mapOutput.put("error", "JSON Parsing Exception");
			mapOutput.put("originalURL", "url");
			resp = new ResponseEntity<>(mapOutput, HttpStatus.UNPROCESSABLE_ENTITY);
		}
		return resp;
	}
	@GetMapping(value = "/healthcheck", consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> healthcheck() {
		logger.info("Healthcheck ");
		return new ResponseEntity<>("OK",  HttpStatus.OK);
	}
	
	@GetMapping(value = "/{shortUrl}", consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getLongUrl(@PathVariable String shortUrl) {
		logger.info("GET: Long URL ");
		ShortUrl obj = repo.getShortUrlByShortUrl(shortUrl);
		String originalUrl=obj.getLongUrl();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(originalUrl));
		return new ResponseEntity<>(headers,  HttpStatus.FOUND);
	}
	 
	
}
