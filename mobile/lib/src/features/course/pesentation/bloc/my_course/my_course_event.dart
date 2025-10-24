import 'package:equatable/equatable.dart';

abstract class MyCourseEvent extends Equatable {
  const MyCourseEvent();
  @override
  List<Object?> get props => [];
}

class LoadMyCourses extends MyCourseEvent {
  final int userId;
  const LoadMyCourses(this.userId);

  @override
  List<Object?> get props => [userId];
}
