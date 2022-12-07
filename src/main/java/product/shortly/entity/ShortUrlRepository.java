package product.shortly.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ShortUrlRepository {

	@Autowired
	private DynamoDBMapper dynamoDBMapper;

	public ShortUrl saveShortUrl(ShortUrl shortUrl) {
		dynamoDBMapper.save(shortUrl);
		return shortUrl;
	}

	public ShortUrl getShortUrlByShortUrl(String shortUrl) {
		return dynamoDBMapper.load(ShortUrl.class, shortUrl);
	}
	
	public String deleteShortUrlByShortUrl(String shortUrl) {
		dynamoDBMapper.delete(dynamoDBMapper.load(ShortUrl.class, shortUrl));
		return "ShortUrl Id : " + shortUrl + " Deleted!";
	}

	public String updateShortUrl(String shortUrl, ShortUrl newObject) {
//		dynamoDBMapper.save(shortUrl, new DynamoDBSaveExpression().withExpectedEntry("id",
//				new ExpectedAttributeValue(new AttributeValue().withS(id))));
		return "";
	}
}