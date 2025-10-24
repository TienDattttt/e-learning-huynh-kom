class MyCourse {
  final int courseId;
  final String name;
  final String image;
  final double progress; // 0 – 100

  MyCourse({
    required this.courseId,
    required this.name,
    required this.image,
    required this.progress,
  });

  // tiện cho UI
  double get progressPercent => progress / 100;
  String get progressLabel => "${progress.toStringAsFixed(0)}%";
  String get completedLabel => progress >= 100 ? "Completed" : "In progress";
}
