import 'package:online_course/src/features/course/data/datasources/course_detail_remote_data_source.dart';
import 'package:online_course/src/features/course/data/mapper/course_detail_mapper.dart';
import 'package:online_course/src/features/course/domain/entities/course_detail.dart';
import 'package:online_course/src/features/course/domain/repositories/course_detail_repository.dart';
import '../datasources/my_course_remote_data_source.dart'; // để check purchased nhanh

class CourseDetailRepositoryImpl implements CourseDetailRepository {
  final CourseDetailRemoteDataSource remote;
  final MyCourseRemoteDataSource myCourseRemote;

  CourseDetailRepositoryImpl(this.remote, this.myCourseRemote);

  @override
  Future<(CourseDetail detail, bool purchased)> getDetail({required int courseId, int? userId}) async {
    // Nếu có userId, thử check purchased nhanh qua /orders/my-courses
    bool purchased = false;
    if (userId != null) {
      try {
        final myCourses = await myCourseRemote.getMyCourses(userId: userId);
        purchased = myCourses.any((c) => c.courseId == courseId);
      } catch (_) {}
    }

    if (purchased && userId != null) {
      final model = await remote.getPrivateDetail(userId, courseId);
      return (CourseDetailMapper.toEntity(model), true);
    } else {
      final model = await remote.getPublicDetail(courseId);
      return (CourseDetailMapper.toEntity(model), false);
    }
  }
}
