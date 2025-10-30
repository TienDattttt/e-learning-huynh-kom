import '../../domain/repositories/learning_repository.dart';
import '../datasources/learning_remote_data_source.dart';

class LearningRepositoryImpl implements LearningRepository {
  final LearningRemoteDataSource remote;
  LearningRepositoryImpl(this.remote);

  @override
  Future<void> saveProgress({
    required int courseId,
    required int lessonId,
    required int progressPercent,
    bool completed = false,
  }) async {
    await remote.saveProgress(
      courseId: courseId,
      lessonId: lessonId,
      progressPercent: progressPercent,
      completed: completed,
    );
  }

  @override
  Future<double> getProgress(int courseId) {
    return remote.getProgress(courseId);
  }
}
