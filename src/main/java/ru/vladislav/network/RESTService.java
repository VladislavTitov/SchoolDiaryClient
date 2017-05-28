package ru.vladislav.network;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.*;
import ru.vladislav.dto.*;

import java.util.List;

public interface RESTService {

    @POST("users")
    Observable<Response<UserDto>> register(@Body UserDtoForRegistration user);

    @POST("login")
    Observable<Response<UserDto>> login(@Header("name") String login, @Header("password") String password);

    @GET("users/{userId}/subjects")
    Observable<Response<List<SubjectDto>>> getAllSubjects(@Header("Auth-Token") String token, @Path("userId") Long userId);

    @GET("users/{userId}/classes")
    Observable<Response<List<ClassDto>>> getAllClasses(@Header("Auth-Token") String token, @Path("userId") Long userId);

    @GET("users/{userId}/lessons")
    Observable<Response<List<LessonDto>>> getAllLessons(@Header("Auth-Token") String token, @Path("userId") Long userId);

    @GET("users/{userId}/classes/{classId}/students")
    Observable<Response<List<StudentDto>>> getStudents(@Header("Auth-Token") String token, @Path("userId") Long userId,
                                                       @Path("classId") Long classId);

    @GET("users/{userId}/lessons/{lessonId}/scores")
    Observable<Response<List<ScoreDto>>> getScore(@Header("Auth-Token") String token, @Path("userId") Long userId,
                                                  @Path("lessonId") Long lessonId);

    @POST("users/{userId}/subjects")
    Observable<Response<SubjectDto>> postNewSubject(@Header("Auth-Token") String token, @Path("userId") Long userId, @Body SubjectDto newSubject);

    @POST("users/{userId}/classes")
    Observable<Response<ClassDto>> postNewClass(@Header("Auth-Token") String token, @Path("userId") Long userId, @Body ClassDto newClass);

    @POST("/users/{userId}/classes/{classId}/students")
    Observable<Response<StudentDto>> postNewStudent(@Header("Auth-Token") String token, @Path("userId") Long userId,
                                                    @Path("classId") Long classId, @Body StudentDto studentDto);

    @POST("users/{userId}/lessons")
    Observable<Response<LessonDto>> postNewLesson(@Header("Auth-Token") String token, @Path("userId") Long userId,
                                                  @Body LessonDto lessonDto);

    @POST("/users/{userId}/lessons/{lessonId}/scores")
    Observable<Response<ScoreDto>> postNewScore(@Header("Auth-Token") String token, @Path("userId") Long userId,
                                                 @Path("lessonId") Long lessonId, @Body ScoreDto scoreDto);
}
