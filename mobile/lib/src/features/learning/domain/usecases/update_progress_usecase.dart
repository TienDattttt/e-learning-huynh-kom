import '../../domain/repositories/learning_repository.dart';

class UpdateProgressUseCase {
  final LearningRepository repo;
  UpdateProgressUseCase(this.repo);

  Future<void> call({
    required int courseId,
    required int lessonId,
    required int progressPercent,
    bool completed = false,
  }) =>
      repo.saveProgress(
        courseId: courseId,
        lessonId: lessonId,
        progressPercent: progressPercent,
        completed: completed,
      );
}
