package telran.college.repo;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import org.springframework.stereotype.Repository;

import telran.college.documents.StudentDoc;
import telran.college.dto.Student;
@Repository
public class StudentAggregationRepositoryImpl implements StudentAggregationRepository {
private static final String AVG_MARK_FIELD = "avgMark";
@Autowired
	MongoTemplate mongoTemplate;
	@Override
	public List<Student> findTopBestStudents(int nStudents) {
		UnwindOperation unwindOperation = unwind("marks");
		GroupOperation groupOperation = group("id", "name").avg("marks.mark").as(AVG_MARK_FIELD);
		SortOperation sortOperation = sort(Direction.DESC, AVG_MARK_FIELD );
		LimitOperation limitOperation = limit(nStudents);
		ProjectionOperation projectionOperation = project().andExclude(AVG_MARK_FIELD);
		Aggregation aggregation = newAggregation(unwindOperation, groupOperation, sortOperation,
				limitOperation, projectionOperation);
		var documents = mongoTemplate.aggregate(aggregation, StudentDoc.class, Document.class);
		return getStudentsResult(documents);
	}
	private List<Student> getStudentsResult(AggregationResults<Document> documents) {
		return documents.getMappedResults().stream().map(this::getStudent).toList();
	}
	private Student getStudent(Document doc) {
		Document idDocument = doc.get("_id", Document.class);
		return new Student(idDocument.getLong("id"), idDocument.getString("name"));
	}
	@Override
	public List<Student> findGoodStudents() {
		ArrayList<AggregationOperation> pipeline = new ArrayList<>();
		pipeline.add(unwind("marks"));
		pipeline.add(group("id", "name").avg("marks.mark").as(AVG_MARK_FIELD));
		double avgMark = getCollegeAvgMark();
		pipeline.add(match(Criteria.where(AVG_MARK_FIELD).gt(avgMark)));
		pipeline.add(project().andExclude(AVG_MARK_FIELD));
		Aggregation aggregation = newAggregation(pipeline);
		var documents = mongoTemplate.aggregate(aggregation, StudentDoc.class, Document.class);
		return getStudentsResult(documents);
	}
	private double getCollegeAvgMark() {
		ArrayList<AggregationOperation> pipeline = new ArrayList<>();
		pipeline.add(unwind("marks"));
		pipeline.add(group().avg("marks.mark").as(AVG_MARK_FIELD));
		Aggregation aggregation = newAggregation(pipeline);
		var document = mongoTemplate.aggregate(aggregation, StudentDoc.class, Document.class)
				.getUniqueMappedResult();
		return document.getDouble(AVG_MARK_FIELD);
	}
	

}
