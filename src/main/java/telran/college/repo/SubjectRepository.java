package telran.college.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.college.documents.SubjectDoc;

public interface SubjectRepository extends MongoRepository<SubjectDoc, Long> {

}
