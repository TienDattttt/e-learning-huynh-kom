import 'package:flutter_bloc/flutter_bloc.dart';
import '../../../domain/usecases/get_course.dart';
import 'course_event.dart';
import 'course_state.dart';

class CourseBloc extends Bloc<CourseEvent, CourseState> {
  final GetCourseUseCase getCourseUseCase;

  CourseBloc({required this.getCourseUseCase}) : super(const CourseInitial()) {
    on<LoadCourseEvent>((event, emit) async {
      emit(const CourseLoading());
      try {
        final courses = await getCourseUseCase();
        emit(CourseLoaded(courses));
      } catch (e) {
        emit(CourseError(e.toString()));
      }
    });
  }
}
