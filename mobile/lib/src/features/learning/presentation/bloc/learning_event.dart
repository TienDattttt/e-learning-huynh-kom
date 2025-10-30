import 'package:equatable/equatable.dart';

abstract class LearningEvent extends Equatable {
  const LearningEvent();
  @override
  List<Object?> get props => [];
}

class UpdateLessonProgress extends LearningEvent {
  final int courseId;
  final int lessonId;
  final int progressPercent;
  final bool completed;

  const UpdateLessonProgress({
    required this.courseId,
    required this.lessonId,
    required this.progressPercent,
    this.completed = false,
  });

  @override
  List<Object?> get props => [courseId, lessonId, progressPercent, completed];
}
