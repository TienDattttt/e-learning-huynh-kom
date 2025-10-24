class CourseModel {
  final int id;
  final String name;
  final String image;
  final String description;
  final int price;
  final int? promotionPrice;

  CourseModel({
    required this.id,
    required this.name,
    required this.image,
    required this.description,
    required this.price,
    this.promotionPrice,
  });

  factory CourseModel.fromJson(Map<String, dynamic> json) {
    return CourseModel(
      id: json['courseId'] as int,
      name: json['name'] ?? '',
      image: json['image'] ?? '',
      description: json['description'] ?? '',
      price: (json['price'] ?? 0) as int,
      promotionPrice: json['promotionPrice'] == null ? null : (json['promotionPrice'] as int),
    );
  }
}
