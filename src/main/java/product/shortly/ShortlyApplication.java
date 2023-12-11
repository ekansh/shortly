package product.shortly;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import product.shortly.entity.ShortUrl;
import product.shortly.entity.ShortUrlRepository;

@SpringBootApplication
@EnableFeignClients
public class ShortlyApplication implements CommandLineRunner{
	Logger logger = LoggerFactory.getLogger(ShortlyApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(ShortlyApplication.class, args);
	}
	@Autowired ShortUrlRepository repo ;
	@Override
	public void run(String... args) throws Exception {
		 ShortUrl shortUrlByShortUrl = repo.getShortUrlByShortUrl("aaaaaps");
		 logger.info("If there is a short url alias for `aaaaaps` then will get the long URL ");
		 logger.info("Long URL:"+shortUrlByShortUrl.getLongUrl());
		
	}

}
