import 'package:online_course/src/features/course/domain/entities/course_detail.dart';
import '../models/course_detail_model.dart';

class CourseDetailMapper {
  static CourseDetail toEntity(CourseDetailModel m) {
    return CourseDetail(
      id: m.id,
      name: m.name,
      description: m.description,
      image: m.image,
      content: m.content,
      price: m.price,
      promotionPrice: m.promotionPrice,
      overallProgress: m.overallProgress,
      chapters: m.chapters.map(_chapter).toList(),
    );
  }

  static CourseChapter _chapter(ChapterModel c) {
    return CourseChapter(
      id: c.id,
      name: c.name,
      order: c.order,
      lessons: c.lessons.map(_lesson).toList(),
    );
  }

  static CourseLesson _lesson(LessonModel l) {
    return CourseLesson(
      id: l.id,
      name: l.name,
      videoPath: l.videoPath,
      slidePath: l.slidePath,
      type: l.type,
      order: l.order,
      progressPercent: l.progressPercent,
      completed: l.completed,
    );
  }
}
