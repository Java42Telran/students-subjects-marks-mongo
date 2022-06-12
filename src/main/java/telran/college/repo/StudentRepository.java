package telran.college.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import telran.college.documents.StudentDoc;
import telran.college.projection.*;

public interface StudentRepository extends MongoRepository<StudentDoc, Long>, StudentAggregationRepository {
//@Query(value="{'marks.subject': ?0, 'marks.mark': {$gte: ?1}}", fields = "{name: 1}")
	List<StudentNameProj> findByMarksSubjectAndMarksMarkGreaterThanEqual(String subjectName, int mark);
//@Query(value = "{'name': ?0, 'marks.subject': ?1}", fields = "{'marks': 1}")
StudentMarksProj findByNameAndMarksSubject(String name, String subjectName);

@Query(value="{'marks.subject': ?1, marks: {$not: {$elemMatch: {subject: ?1, mark: {$lt: ?0}}}}}", fields="{name: 1}")
List<StudentDoc> findStdentsAllMarksSubjectGreater(int mark, String subject);


}
