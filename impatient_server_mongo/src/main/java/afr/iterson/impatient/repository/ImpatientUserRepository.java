package afr.iterson.impatient.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import afr.iterson.impatient.model.ImpatientUser;

public interface ImpatientUserRepository extends MongoRepository<ImpatientUser, Long>
{
	public ImpatientUser findByUsernameAndPassword(String username, String password);
	public ImpatientUser findByUsername(String username);
}
