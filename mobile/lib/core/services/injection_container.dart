import 'package:get_it/get_it.dart';

// ========== COURSE IMPORTS ==========
import 'package:online_course/src/features/course/data/datasources/course_remote_data_source.dart';
import 'package:online_course/src/features/course/data/repositories/course_repository_impl.dart';
import 'package:online_course/src/features/course/domain/repositories/course_repository.dart';
import 'package:online_course/src/features/course/domain/usecases/get_course.dart';
import 'package:online_course/src/features/course/domain/usecases/get_feature_course.dart';
import 'package:online_course/src/features/course/domain/usecases/get_recommend_course.dart';
import 'package:online_course/src/features/course/pesentation/bloc/explore/course_bloc.dart';
import 'package:online_course/src/features/course/pesentation/bloc/favorite_course/favorite_course_bloc.dart';
import 'package:online_course/src/features/course/pesentation/bloc/feature/feature_course_bloc.dart';
import 'package:online_course/src/features/course/pesentation/bloc/recommend/recommend_course_bloc.dart';

// ========== AUTH IMPORTS (THÊM MỚI) ==========
import 'package:online_course/src/features/auth/data/datasources/auth_remote_data_source.dart';
import 'package:online_course/src/features/auth/data/repositories/auth_repository_impl.dart';
import 'package:online_course/src/features/auth/domain/repositories/auth_repository.dart';
import 'package:online_course/src/features/auth/domain/usecases/login_usecase.dart';
import 'package:online_course/src/features/auth/presentation/bloc/login/login_bloc.dart';

final locator = GetIt.instance;

Future initLocator() async {
  // ================= AUTH =================
  locator
    ..registerLazySingleton<AuthRemoteDataSource>(() => AuthRemoteDataSource())
    ..registerLazySingleton<AuthRepository>(() => AuthRepositoryImpl(locator()))
    ..registerLazySingleton(() => LoginUseCase(locator()))
    ..registerFactory(() => LoginBloc(locator()));

  // ================= COURSE =================
  locator
    ..registerLazySingleton<CourseRemoteDataSource>(() => CourseRemoteDataSourceImpl())
    ..registerLazySingleton<CourseRepository>(() => CourseRepositoryImpl(locator()))
    ..registerLazySingleton(() => GetCourseUseCase(locator()))
    ..registerLazySingleton(() => GetFeatureCourseUseCase(locator()))
    ..registerLazySingleton(() => GetRecommendCourseUserCase(locator()))
    ..registerFactory(() => CourseBloc(
        getCourseUseCase: locator(),
        getFeaturedCourseUseCase: locator()))
    ..registerFactory(() => FeatureCourseBloc(locator()))
    ..registerFactory(() => RecommendCourseBloc(locator()))
    ..registerFactory(() => FavoriteCourseBloc());
}
