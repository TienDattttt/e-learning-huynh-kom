abstract class LearningRepository {
  Future<void> saveProgress({
    required int courseId,
    required int lessonId,
    required int progressPercent,
    bool completed,
  });

  Future<double> getProgress(int courseId);
}
