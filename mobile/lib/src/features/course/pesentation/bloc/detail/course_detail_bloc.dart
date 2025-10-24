import 'package:flutter_bloc/flutter_bloc.dart';
import '../../../domain/usecases/get_course_detail.dart';
import 'course_detail_event.dart';
import 'course_detail_state.dart';

class CourseDetailBloc extends Bloc<CourseDetailEvent, CourseDetailState> {
  final GetCourseDetailUseCase usecase;
  CourseDetailBloc(this.usecase) : super(const CourseDetailInitial()) {
    on<LoadCourseDetail>((event, emit) async {
      emit(const CourseDetailLoading());
      try {
        final (detail, purchased) = await usecase(courseId: event.courseId, userId: event.userId);
        emit(CourseDetailLoaded(detail: detail, purchased: purchased));
      } catch (e) {
        emit(CourseDetailError(e.toString()));
      }
    });
  }
}
