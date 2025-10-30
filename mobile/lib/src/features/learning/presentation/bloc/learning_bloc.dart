import 'package:flutter_bloc/flutter_bloc.dart';
import 'learning_event.dart';
import 'learning_state.dart';
import '../../../domain/usecases/update_progress_usecase.dart';

class LearningBloc extends Bloc<LearningEvent, LearningState> {
  final UpdateProgressUseCase updateProgress;

  LearningBloc(this.updateProgress) : super(LearningInitial()) {
    on<UpdateLessonProgress>((event, emit) async {
      try {
        await updateProgress(
          courseId: event.courseId,
          lessonId: event.lessonId,
          progressPercent: event.progressPercent,
          completed: event.completed,
        );
        emit(LearningUpdated());
      } catch (e) {
        emit(LearningError(e.toString()));
      }
    });
  }
}
