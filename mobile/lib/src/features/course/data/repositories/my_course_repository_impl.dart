import '../../domain/entities/my_course.dart';
import '../../domain/repositories/my_course_repository.dart';
import '../datasources/my_course_remote_data_source.dart';
import '../models/my_course_model.dart';

class MyCourseRepositoryImpl implements MyCourseRepository {
  final MyCourseRemoteDataSource remote;

  MyCourseRepositoryImpl(this.remote);

  @override
  Future<List<MyCourse>> getMyCoursesWithProgress({required int userId}) async {
    final models = await remote.getMyCourses(userId: userId);

    // song song gọi progress
    final futures = models.map((m) async {
      final prog = await remote.getProgress(userId: userId, courseId: m.courseId);
      return _toEntity(m, prog);
    }).toList();

    return Future.wait(futures);
  }

  MyCourse _toEntity(MyCourseModel m, double progress) {
    return MyCourse(
      courseId: m.courseId,
      name: m.name,
      image: m.image,
      progress: progress,
    );
  }
}
