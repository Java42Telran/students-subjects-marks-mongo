package telran.college.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import telran.college.documents.StudentDoc;
import telran.college.projection.StudentNameProj;

public interface StudentRepository extends MongoRepository<StudentDoc, Long> {
@Query(value="{'marks.subject': ?0, 'marks.mark': {$gte: ?1}}", fields = "{name: 1}")
	List<StudentNameProj> findByMarksSubjectAndMarksMarkGreaterThanEqual(String subjectName, int mark);

}
