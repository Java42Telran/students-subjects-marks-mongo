package telran.college.repo;

import java.util.List;

import telran.college.dto.Student;

public interface StudentAggregationRepository {
List<Student> findTopBestStudents(int nStudents);
List<Student> findGoodStudents();
}
