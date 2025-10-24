import 'package:equatable/equatable.dart';
import '../../../domain/entities/course_detail.dart';

abstract class CourseDetailState extends Equatable {
  const CourseDetailState();
  @override
  List<Object?> get props => [];
}

class CourseDetailInitial extends CourseDetailState {
  const CourseDetailInitial();
}

class CourseDetailLoading extends CourseDetailState {
  const CourseDetailLoading();
}

class CourseDetailLoaded extends CourseDetailState {
  final CourseDetail detail;
  final bool purchased;

  const CourseDetailLoaded({required this.detail, required this.purchased});

  @override
  List<Object?> get props => [detail, purchased];
}

class CourseDetailError extends CourseDetailState {
  final String message;
  const CourseDetailError(this.message);

  @override
  List<Object?> get props => [message];
}
