import '../entities/my_course.dart';
import '../repositories/my_course_repository.dart';

class GetMyCoursesUseCase {
  final MyCourseRepository repo;
  GetMyCoursesUseCase(this.repo);

  Future<List<MyCourse>> call({required int userId}) {
    return repo.getMyCoursesWithProgress(userId: userId);
  }
}
