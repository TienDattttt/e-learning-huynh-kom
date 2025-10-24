import '../entities/course_detail.dart';

abstract class CourseDetailRepository {
  Future<(CourseDetail detail, bool purchased)> getDetail({required int courseId, int? userId});
}
