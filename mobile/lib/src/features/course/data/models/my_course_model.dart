class MyCourseModel {
  final int courseId;
  final String name;
  final String image;
  final String purchasedAt;

  MyCourseModel({
    required this.courseId,
    required this.name,
    required this.image,
    required this.purchasedAt,
  });

  factory MyCourseModel.fromJson(Map<String, dynamic> json) {
    return MyCourseModel(
      courseId: json["courseId"],   // <-- CHUẨN HÓA LẠI TÊN NÀY
      name: json["name"],
      image: json["image"],
      purchasedAt: json["purchasedAt"],
    );
  }
}
