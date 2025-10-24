class CourseDetailModel {
  final int id;
  final String name;
  final String description;
  final String image;
  final String content;
  final int price;
  final int? promotionPrice;
  final double overallProgress;
  final List<ChapterModel> chapters;

  CourseDetailModel({
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

  factory CourseDetailModel.fromPublicJson(Map<String, dynamic> json) {
    final data = json['data'] as Map<String, dynamic>;
    return CourseDetailModel(
      id: data['courseId'],
      name: data['name'] ?? '',
      description: data['description'] ?? '',
      image: data['image'] ?? '',
      content: data['content'] ?? '',
      price: (data['price'] ?? 0) as int,
      promotionPrice: data['promotionPrice'],
      overallProgress: 0.0,
      chapters: ((data['chapters'] as List?) ?? [])
          .map((e) => ChapterModel.fromJson(e as Map<String, dynamic>, isPrivate: false))
          .toList(),
    );
  }

  factory CourseDetailModel.fromPrivateJson(Map<String, dynamic> json) {
    final data = json['data'] as Map<String, dynamic>;
    return CourseDetailModel(
      id: data['courseId'],
      name: data['name'] ?? '',
      description: data['description'] ?? '',
      image: data['image'] ?? '',
      content: data['content'] ?? '',
      price: (data['price'] ?? 0) as int? ?? 0,
      promotionPrice: data['promotionPrice'],
      overallProgress: (data['overallProgress'] ?? 0).toDouble(),
      chapters: ((data['chapters'] as List?) ?? [])
          .map((e) => ChapterModel.fromJson(e as Map<String, dynamic>, isPrivate: true))
          .toList(),
    );
  }
}

class ChapterModel {
  final int id;
  final String name;
  final int order;
  final List<LessonModel> lessons;

  ChapterModel({
    required this.id,
    required this.name,
    required this.order,
    required this.lessons,
  });

  factory ChapterModel.fromJson(Map<String, dynamic> json, {required bool isPrivate}) {
    return ChapterModel(
      id: json['chapterId'],
      name: json['nameChapter'] ?? '',
      order: json['orderChapter'] ?? 0,
      lessons: ((json['lessons'] as List?) ?? [])
          .map((e) => LessonModel.fromJson(e as Map<String, dynamic>, isPrivate: isPrivate))
          .toList(),
    );
  }
}

class LessonModel {
  final int id;
  final String name;
  final String? videoPath;
  final String? slidePath;
  final String type;
  final int order;
  final double? progressPercent;
  final bool? completed;

  LessonModel({
    required this.id,
    required this.name,
    required this.videoPath,
    required this.slidePath,
    required this.type,
    required this.order,
    this.progressPercent,
    this.completed,
  });

  factory LessonModel.fromJson(Map<String, dynamic> json, {required bool isPrivate}) {
    return LessonModel(
      id: json['lessonId'],
      name: json['name'] ?? '',
      videoPath: json['videoPath'],
      slidePath: json['slidePath'],
      type: json['typeDocument'] ?? 'video',
      order: json['sortOrder'] ?? 0,
      progressPercent: isPrivate ? (json['progressPercent'] ?? 0).toDouble() : null,
      completed: isPrivate ? (json['completed'] ?? false) : null,
    );
  }
}
