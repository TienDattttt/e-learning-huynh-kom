import 'package:flutter_bloc/flutter_bloc.dart';
import '../../../domain/usecases/get_my_courses.dart';
import 'my_course_event.dart';
import 'my_course_state.dart';

class MyCourseBloc extends Bloc<MyCourseEvent, MyCourseState> {
  final GetMyCoursesUseCase usecase;

  MyCourseBloc(this.usecase) : super(const MyCourseInitial()) {
    on<LoadMyCourses>((event, emit) async {
      emit(const MyCourseLoading());
      try {
        final list = await usecase(userId: event.userId);
        final completed = list.where((e) => e.progress >= 100.0).toList();
        final progress = list.where((e) => e.progress > 0.0 && e.progress < 100.0).toList();

        // nếu tất cả đều 0% -> coi như đang học để UI không trống
        if (progress.isEmpty && completed.isEmpty) {
          emit(MyCourseLoaded(progressCourses: list, completedCourses: const []));
        } else {
          emit(MyCourseLoaded(progressCourses: progress, completedCourses: completed));
        }
      } catch (e) {
        emit(MyCourseError(e.toString()));
      }
    });
  }
}
