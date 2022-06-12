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
import telran.college.dto.*;
@Repository
public class StudentAggregationRepositoryImpl implements StudentAggregationRepository {
private static final String AVG_MARK_FIELD = "avgMark";
private static final String COUNT_FIELD = "count";
@Autowired
	MongoTemplate mongoTemplate;
	@Override
	public List<Student> findTopBestStudents(int nStudents, String subjectName) {
		ArrayList<AggregationOperation> operations = new ArrayList<>();
		operations.add(unwind("marks"));
		if (subjectName != null) {
			operations.add(match(Criteria.where("marks.subject").is(subjectName)));
		}
		
		operations.add(group("id", "name").avg("marks.mark").as(AVG_MARK_FIELD)) ;
		operations.add(sort(Direction.DESC, AVG_MARK_FIELD) );
		operations.add(limit(nStudents));
		operations.add(project().andExclude(AVG_MARK_FIELD));
		Aggregation aggregation = newAggregation(operations);
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
	@Override
	public String findSubjectGreatestAvgMark() {
		ArrayList<AggregationOperation> operations = new ArrayList<>();
		operations.add(unwind("marks"));
		operations.add(group("marks.subject").avg("marks.mark").as(AVG_MARK_FIELD));
		operations.add(sort(Direction.DESC, AVG_MARK_FIELD));
		operations.add(limit(1));
		operations.add(project().andExclude(AVG_MARK_FIELD));
		Aggregation aggregation = newAggregation(operations);
		var document = mongoTemplate.aggregate(aggregation, StudentDoc.class, Document.class)
				.getUniqueMappedResult();
		
		
		return document.getString("_id");
	}
	@Override
	public List<String> findSubjectsAvgMarkGreater(double avgMark) {
		ArrayList<AggregationOperation> operations = new ArrayList<>();
		operations.add(unwind("marks"));
		operations.add(group("marks.subject").avg("marks.mark").as(AVG_MARK_FIELD));
		operations.add(match(Criteria.where(AVG_MARK_FIELD).gte(avgMark)));
		operations.add(project().andExclude(AVG_MARK_FIELD));
		Aggregation aggregation = newAggregation(operations);
		var documents = mongoTemplate.aggregate(aggregation, StudentDoc.class, Document.class)
				.getMappedResults();
		
		
		return documents.stream().map(d -> d.getString("_id")).toList();
	}
	@Override
	public List<Student> findStudentsMaxMarks() {
		ArrayList<AggregationOperation> operations = new ArrayList<>();
		operations.add(unwind("marks"));
		operations.add(group("id", "name").count().as(COUNT_FIELD)) ;
		operations.add(match(Criteria.where(COUNT_FIELD).is(getMaxCount())));
		operations.add(project().andExclude(COUNT_FIELD));
		Aggregation aggregation = newAggregation(operations);
		var documents = mongoTemplate.aggregate(aggregation, StudentDoc.class, Document.class);
		return getStudentsResult(documents);
	}
	private int getMaxCount() {
		ArrayList<AggregationOperation> operations = new ArrayList<>();
		operations.add(unwind("marks"));
		operations.add(group("id").count().as(COUNT_FIELD)) ;
		operations.add(sort(Direction.DESC, COUNT_FIELD));
		operations.add(limit(1));
		Aggregation aggregation = newAggregation(operations);
		var document = mongoTemplate.aggregate(aggregation, StudentDoc.class, Document.class)
				.getUniqueMappedResult();
		
		return document.getInteger(COUNT_FIELD);
	}
	@Override
	public List<Long> findStudentIdsAvgMarkLess(double avgMark) {
		ArrayList<AggregationOperation> operations = new ArrayList<>();
		operations.add(unwind("marks", true));
		operations.add(group("id").avg("marks.mark").as(AVG_MARK_FIELD));
		operations.add(match(new Criteria()
				.orOperator(Criteria.where(AVG_MARK_FIELD).isNull(),
						Criteria.where(AVG_MARK_FIELD).lt(avgMark))));
		operations.add(project().andExclude(AVG_MARK_FIELD));
		var documents = mongoTemplate.aggregate(newAggregation(operations), StudentDoc.class,
				Document.class).getMappedResults();
		return documents.stream().map(d -> d.getLong("_id")).toList();
	}
	@Override
	public List<Student> findStudentsMarksCountLess(int count) {
		ArrayList<AggregationOperation> operations = new ArrayList<>();
		operations.add(unwind("marks", true));
		operations.add(group("id", "name").count().as(COUNT_FIELD)) ;
		operations.add(match(Criteria.where(COUNT_FIELD).lt(count)));
		operations.add(project().andExclude(COUNT_FIELD));
		Aggregation aggregation = newAggregation(operations);
		var documents = mongoTemplate.aggregate(aggregation, StudentDoc.class, Document.class);
		return getStudentsResult(documents);
	}
	

}
