import '../entities/my_course.dart';

abstract class MyCourseRepository {
  Future<List<MyCourse>> getMyCoursesWithProgress({required int userId});
}
