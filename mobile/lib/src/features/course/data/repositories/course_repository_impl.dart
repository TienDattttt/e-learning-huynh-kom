import '../../domain/entities/course.dart';
import '../../domain/repositories/course_repository.dart';
import '../datasources/course_remote_data_source.dart';
import '../mapper/course_mapper.dart';

class CourseRepositoryImpl implements CourseRepository {
  final CourseRemoteDataSource remote;
  CourseRepositoryImpl(this.remote);

  @override
  Future<List<Course>> getCourses() async {
    final models = await remote.getCourses(page: 0, size: 20);
    return models.map(CourseMapper.toEntity).toList();
  }
}
