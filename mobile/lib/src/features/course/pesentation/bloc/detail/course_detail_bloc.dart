import 'package:flutter_bloc/flutter_bloc.dart';
import '../../../domain/usecases/get_course_detail.dart';
import '../../../domain/usecases/buy_course_usecase.dart';
import 'course_detail_event.dart';
import 'course_detail_state.dart';

class CourseDetailBloc extends Bloc<CourseDetailEvent, CourseDetailState> {
  final GetCourseDetailUseCase getUseCase;
  final BuyCourseUseCase buyUseCase;

  CourseDetailBloc({
    required this.getUseCase,
    required this.buyUseCase,
  }) : super(const CourseDetailInitial()) {
    on<LoadCourseDetail>(_onLoadDetail);
    on<BuyCourseEvent>(_onBuyCourse);
  }

  Future<void> _onLoadDetail(
      LoadCourseDetail event, Emitter<CourseDetailState> emit) async {
    emit(const CourseDetailLoading());
    try {
      final (detail, purchased) =
      await getUseCase(courseId: event.courseId, userId: event.userId);
      emit(CourseDetailLoaded(detail: detail, purchased: purchased));
    } catch (e) {
      emit(CourseDetailError(e.toString()));
    }
  }

  Future<void> _onBuyCourse(
      BuyCourseEvent event, Emitter<CourseDetailState> emit) async {
    emit(const CourseDetailLoading());
    try {
      final (ok, msg) = await buyUseCase(event.courseId);
      if (ok) {
        final (detail, purchased) =
        await getUseCase(courseId: event.courseId, userId: event.userId);
        emit(CourseDetailLoaded(detail: detail, purchased: purchased));
      } else {
        emit(CourseDetailError("Buy failed: $msg"));
      }
    } catch (e) {
      emit(CourseDetailError(e.toString()));
    }
  }
}
