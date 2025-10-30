import 'package:equatable/equatable.dart';

abstract class CourseDetailEvent extends Equatable {
  const CourseDetailEvent();
  @override
  List<Object?> get props => [];
}

class LoadCourseDetail extends CourseDetailEvent {
  final int courseId;
  final int? userId; // null nếu chưa đăng nhập

  const LoadCourseDetail({required this.courseId, this.userId});
  @override
  List<Object?> get props => [courseId, userId];
}

class BuyCourseEvent extends CourseDetailEvent {
  final int courseId;
  final int? userId;
  const BuyCourseEvent({required this.courseId, this.userId});
  @override
  List<Object?> get props => [courseId, userId];
}
