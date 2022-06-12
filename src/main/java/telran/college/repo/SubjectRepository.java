package telran.college.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.college.documents.SubjectDoc;

public interface SubjectRepository extends MongoRepository<SubjectDoc, Long> {

	SubjectDoc findBySubjectName(String subjectName);
	List<SubjectDoc> findBySubjectNameIn(List<String> names);
	List<SubjectDoc> findBySubjectNameNotIn(List<String> names);

}
