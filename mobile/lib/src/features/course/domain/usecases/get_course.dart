import '../entities/course.dart';
import '../repositories/course_repository.dart';

class GetCourseUseCase {
  final CourseRepository repo;
  GetCourseUseCase(this.repo);

  Future<List<Course>> call() => repo.getCourses();
}
