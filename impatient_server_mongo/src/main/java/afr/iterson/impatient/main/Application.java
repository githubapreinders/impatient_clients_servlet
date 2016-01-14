package afr.iterson.impatient.main;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.MultiPartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import afr.iterson.impatient.auth.OAuth2SecurityConfiguration;
import afr.iterson.impatient.controller.AdminControllerOps;
import afr.iterson.impatient.controller.PatientControllerOps;
import afr.iterson.impatient.json.ResourcesMapper;
import afr.iterson.impatient.model.BannerLoader;
import afr.iterson.impatient.model.ImpatientUser;
import afr.iterson.impatient.model.Patient;
import afr.iterson.impatient.model.WaitingQueue;
import afr.iterson.impatient.repository.ImpatientUserRepository;
import afr.iterson.impatient.repository.MongoPatientRepo;
import afr.iterson.impatient.repository.MongoWaitingQueueRepo;
import afr.iterson.impatient.utils.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.Mongo;

//Tell Spring to automatically inject any dependencies that are marked in
//our classes with @Autowired
@EnableAutoConfiguration
@EnableWebMvc
@Configuration
@EnableMongoRepositories(basePackageClasses = { MongoPatientRepo.class, MongoWaitingQueueRepo.class,
		ImpatientUserRepository.class })
@PropertySource("classpath:application.properties")
@ComponentScan("afr.iterson.impatient.controller")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import(OAuth2SecurityConfiguration.class)
public class Application extends RepositoryRestMvcConfiguration
{

	public static void main(String[] args)
	{
		SpringApplication app = new SpringApplication(Application.class);
		app.setShowBanner(false);
		app.run(args);
		// SpringApplication.run(Application.class, args);
	}

	// Trying to load my own banner TODO : getting it at the front instead of
	// somewhere at the end
	@Value("classpath:banner.txt")
	private Resource banner;

	@Bean
	public BannerLoader bannerLoader()
	{
		BannerLoader bl = new BannerLoader();
		bl.setBanner(banner);
		return bl;
	}

	// Hardcoding two staff members, 12 random users and a known user into the
	// system
	@Bean
	public CommandLineRunner runCommands()
	{
		return new CommandLineRunner()
		{
			@Autowired
			MongoPatientRepo repo;

			@Autowired
			ImpatientUserRepository userRepo;

			@SuppressWarnings("unchecked")
			@Override
			public void run(String... args) throws Exception
			{
				userRepo.deleteAll();
				repo.deleteAll();
				for (int i = 0; i < 100; i++)
				{
					Patient p = Utils.makeRandomPatient();
					repo.save(p);
					userRepo.save(new ImpatientUser(p.getMedicalRecordId(), p.getAccessCode(), true, "ROLE_USER"));
				}
				userRepo.save(new ImpatientUser("doug", "pass", true, "ROLE_ADMIN"));
				userRepo.save(new ImpatientUser("jules", "pass", true, "ROLE_ADMIN"));

				Patient p = Utils.makeRandomPatient();
				p.setAppointmentDate(Utils.makeAppointmentDateForToday().getTimeInMillis());
				p.setMedicalRecordId("9881MU");
				p.setAccessCode("PM8Y");
				repo.save(p);
				userRepo.save(new ImpatientUser("9881MU", "PM8Y", true, "ROLE_USER"));
			}
		};
	}

	@Bean
	WaitingQueue getWaitingQueue()
	{
		return new WaitingQueue();
	}

	@Bean
	public PatientControllerOps getPatientControllerOperations()
	{
		return new PatientControllerOps();
	}

	@Bean
	public AdminControllerOps getAdmindControllerOperations()
	{
		return new AdminControllerOps();
	}

	// We are overriding the bean that RepositoryRestMvcConfiguration
	// is using to convert our objects into JSON so that we can control
	// the format. The Spring dependency injection will inject our instance
	// of ObjectMapper in all of the spring data rest classes that rely
	// on the ObjectMapper. This is an example of how Spring dependency
	// injection allows us to easily configure dependencies in code that
	// we don't have easy control over otherwise.
	@Override
	public ObjectMapper halObjectMapper()
	{
		return new ResourcesMapper();
	}

	@Bean
	public MultipartConfigElement multipartConfigElement()
	{
		final MultiPartConfigFactory factory = new MultiPartConfigFactory();
		factory.setMaxFileSize(150);
		factory.setMaxRequestSize(150);
		return factory.createMultipartConfig();
	}

	@SuppressWarnings("deprecation")
	public @Bean MongoDbFactory mongoDbFactory() throws Exception
	{
		return new SimpleMongoDbFactory(new Mongo(), "test");
	}

	public @Bean MongoTemplate mongoTemplate() throws Exception
	{
		return new MongoTemplate(mongoDbFactory());
	}

}
