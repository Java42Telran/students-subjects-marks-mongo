package telran.college.repo;

import java.util.List;

import telran.college.dto.*;

public interface StudentAggregationRepository {
List<Student> findTopBestStudents(int nStudents, String subject);
List<Student> findGoodStudents();
String findSubjectGreatestAvgMark();
List<String> findSubjectsAvgMarkGreater(double avgMark);
List<Student> findStudentsMaxMarks();
List<Long> findStudentIdsAvgMarkLess(double avgMark);
List<Student> findStudentsMarksCountLess(int count);


}
