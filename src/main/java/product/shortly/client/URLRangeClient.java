package product.shortly.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "range", url = "${range.generator.url}")
public interface URLRangeClient {
	@RequestMapping(method = RequestMethod.GET, value = "/hash_range")
	String getHashRange(@RequestParam("name") String machineName);
}
