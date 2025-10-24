class CourseDetail {
  final int id;
  final String name;
  final String description;
  final String image;
  final String content;
  final int price;
  final int? promotionPrice;
  final double overallProgress; // private detail có, public = 0
  final List<CourseChapter> chapters;

  CourseDetail({
    required this.id,
    required this.name,
    required this.description,
    required this.image,
    required this.content,
    required this.price,
    required this.promotionPrice,
    required this.overallProgress,
    required this.chapters,
  });
}

class CourseChapter {
  final int id;
  final String name;
  final int order;
  final List<CourseLesson> lessons;

  CourseChapter({
    required this.id,
    required this.name,
    required this.order,
    required this.lessons,
  });
}

class CourseLesson {
  final int id;
  final String name;
  final String? videoPath;
  final String? slidePath;
  final String type; // "video"/"doc"
  final int order;
  final double? progressPercent; // private có
  final bool? completed;        // private có

  CourseLesson({
    required this.id,
    required this.name,
    required this.videoPath,
    required this.slidePath,
    required this.type,
    required this.order,
    this.progressPercent,
    this.completed,
  });
}
