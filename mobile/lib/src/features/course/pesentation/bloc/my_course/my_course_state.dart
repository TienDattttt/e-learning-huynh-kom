import 'package:equatable/equatable.dart';
import '../../../domain/entities/my_course.dart';

abstract class MyCourseState extends Equatable {
  const MyCourseState();
  @override
  List<Object?> get props => [];
}

class MyCourseInitial extends MyCourseState {
  const MyCourseInitial();
}

class MyCourseLoading extends MyCourseState {
  const MyCourseLoading();
}

class MyCourseLoaded extends MyCourseState {
  final List<MyCourse> progressCourses;  // <=== PHẢI CÓ
  final List<MyCourse> completedCourses; // <=== PHẢI CÓ

  const MyCourseLoaded({
    required this.progressCourses,
    required this.completedCourses,
  });

  @override
  List<Object?> get props => [progressCourses, completedCourses];
}

class MyCourseError extends MyCourseState {
  final String message;
  const MyCourseError(this.message);

  @override
  List<Object?> get props => [message];
}
