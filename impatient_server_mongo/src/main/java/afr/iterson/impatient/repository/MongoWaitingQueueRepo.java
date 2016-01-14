package afr.iterson.impatient.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import afr.iterson.impatient.model.WaitingQueue;

public interface MongoWaitingQueueRepo extends MongoRepository<WaitingQueue, Long>
{

}
