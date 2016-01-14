package afr.iterson.impatient.main;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.Mongo;

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration
{

	@SuppressWarnings("deprecation")
	public  @Bean MongoDbFactory mongoDbFactory() throws Exception
	{
		return new SimpleMongoDbFactory(new Mongo(), "patientdb");
	}

	public  @Bean MongoTemplate mongoTemplate() throws Exception
	{
		return new MongoTemplate(mongoDbFactory());
	}

	@Override
	protected String getDatabaseName()
	{
		return "test";
	}

	@Override
	@Bean
	public Mongo mongo() throws Exception
	{
		return new Mongo("localhost");
	}

}