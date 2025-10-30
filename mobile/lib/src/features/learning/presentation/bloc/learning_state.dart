import 'package:equatable/equatable.dart';

abstract class LearningState extends Equatable {
  const LearningState();
  @override
  List<Object?> get props => [];
}

class LearningInitial extends LearningState {}
class LearningUpdated extends LearningState {}
class LearningError extends LearningState {
  final String message;
  const LearningError(this.message);
}
