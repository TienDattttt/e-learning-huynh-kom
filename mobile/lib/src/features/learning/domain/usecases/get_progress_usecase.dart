import '../repositories/learning_repository.dart';

class GetProgressUseCase {
  final LearningRepository repo;
  GetProgressUseCase(this.repo);

  Future<double> call(int courseId) => repo.getProgress(courseId);
}
