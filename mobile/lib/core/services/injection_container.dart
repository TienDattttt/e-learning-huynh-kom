import 'package:get_it/get_it.dart';

// COURSE
import 'package:online_course/src/features/course/data/datasources/course_remote_data_source.dart';
import 'package:online_course/src/features/course/data/repositories/course_repository_impl.dart';
import 'package:online_course/src/features/course/domain/repositories/course_repository.dart';
import 'package:online_course/src/features/course/domain/usecases/get_course.dart';
import 'package:online_course/src/features/course/pesentation/bloc/explore/course_bloc.dart';

// AUTH
import 'package:online_course/src/features/auth/data/datasources/auth_remote_data_source.dart';
import 'package:online_course/src/features/auth/data/repositories/auth_repository_impl.dart';
import 'package:online_course/src/features/auth/domain/repositories/auth_repository.dart';
import 'package:online_course/src/features/auth/domain/usecases/login_usecase.dart';
import 'package:online_course/src/features/auth/presentation/bloc/login/login_bloc.dart';

// MY COURSES
import 'package:online_course/src/features/course/data/datasources/my_course_remote_data_source.dart';
import 'package:online_course/src/features/course/data/repositories/my_course_repository_impl.dart';
import 'package:online_course/src/features/course/domain/repositories/my_course_repository.dart';
import 'package:online_course/src/features/course/domain/usecases/get_my_courses.dart';
import 'package:online_course/src/features/course/pesentation/bloc/my_course/my_course_bloc.dart';

// COURSE DETAIL
import 'package:online_course/src/features/course/data/datasources/course_detail_remote_data_source.dart';
import 'package:online_course/src/features/course/data/repositories/course_detail_repository_impl.dart';
import 'package:online_course/src/features/course/domain/repositories/course_detail_repository.dart';
import 'package:online_course/src/features/course/domain/usecases/get_course_detail.dart';
import 'package:online_course/src/features/course/pesentation/bloc/detail/course_detail_bloc.dart';

// ORDER (thêm mới)
import 'package:online_course/src/features/course/domain/usecases/buy_course_usecase.dart';
import 'package:online_course/src/features/course/data/order_api.dart';

final locator = GetIt.instance;

Future initLocator() async {
  // ========== AUTH ==========
  locator
    ..registerLazySingleton<AuthRemoteDataSource>(() => AuthRemoteDataSource())
    ..registerLazySingleton<AuthRepository>(() => AuthRepositoryImpl(locator()))
    ..registerLazySingleton(() => LoginUseCase(locator()))
    ..registerFactory(() => LoginBloc(locator()));

  // ========== COURSE (Explore) ==========
  locator
    ..registerLazySingleton<CourseRemoteDataSource>(() => CourseRemoteDataSourceImpl())
    ..registerLazySingleton<CourseRepository>(() => CourseRepositoryImpl(locator()))
    ..registerLazySingleton(() => GetCourseUseCase(locator()))
    ..registerFactory(() => CourseBloc(getCourseUseCase: locator()));

  // ========== MY COURSES ==========
  locator
    ..registerLazySingleton<MyCourseRemoteDataSource>(() => MyCourseRemoteDataSourceImpl())
    ..registerLazySingleton<MyCourseRepository>(() => MyCourseRepositoryImpl(locator()))
    ..registerLazySingleton(() => GetMyCoursesUseCase(locator()))
    ..registerFactory(() => MyCourseBloc(locator()));

  // ========== COURSE DETAIL ==========
  locator
    ..registerLazySingleton<CourseDetailRemoteDataSource>(() => CourseDetailRemoteDataSourceImpl())
    ..registerLazySingleton<CourseDetailRepository>(() => CourseDetailRepositoryImpl(
      locator<CourseDetailRemoteDataSource>(),
      locator<MyCourseRemoteDataSource>(),
    ))
    ..registerLazySingleton(() => GetCourseDetailUseCase(locator()))

  // 🟢 Thêm API & UseCase mua khóa học
    ..registerLazySingleton(() => OrderApi())
    ..registerLazySingleton(() => BuyCourseUseCase(locator()))

  // Bloc cần cả hai usecase
    ..registerFactory(() => CourseDetailBloc(
      getUseCase: locator<GetCourseDetailUseCase>(),
      buyUseCase: locator<BuyCourseUseCase>(),
    ));
}
