import '../entities/course_detail.dart';
import '../repositories/course_detail_repository.dart';

class GetCourseDetailUseCase {
  final CourseDetailRepository repo;
  GetCourseDetailUseCase(this.repo);

  Future<(CourseDetail detail, bool purchased)> call({required int courseId, int? userId}) {
    return repo.getDetail(courseId: courseId, userId: userId);
  }
}
