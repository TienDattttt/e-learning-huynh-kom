import '../entities/course.dart';

abstract class CourseRepository {
  Future<List<Course>> getCourses();
}
