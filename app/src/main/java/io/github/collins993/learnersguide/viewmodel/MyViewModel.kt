package io.github.collins993.learnersguide.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.github.collins993.learnersguide.model.CourseResponse
import io.github.collins993.learnersguide.repository.Repository
import io.github.collins993.learnersguide.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MyViewModel(
    app: Application,
    private val repository: Repository
): AndroidViewModel(app) {


    val courseList: MutableLiveData<Resource<CourseResponse>> = MutableLiveData()
    var courseResponse : CourseResponse? = null

    init {
        getCourseList()
    }

    fun getCourseList() = viewModelScope.launch {
        courseList.postValue(Resource.Loading())
        try {
                val response = repository.getCourseList()
            courseList.postValue(courseListResponse(response))

                //breakingNews.postValue(Resource.Error("No Internet Connection"))


        }catch (t: Throwable) {
            when(t){
                is IOException -> courseList.postValue(Resource.Error("Network Failure"))
                else -> courseList.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun courseListResponse(response: Response<CourseResponse>): Resource<CourseResponse> {
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->

                //Pagination by saving initial response in old article and adding it to new article
                //breakingNewsPage++

                if (courseResponse == null){
                    courseResponse = resultResponse
                }
//                else{
//                    val oldArticles = breakingNewsResponse?.articles
//                    val newArticles = resultResponse.articles
//                    oldArticles?.addAll(newArticles)
//                }
                return Resource.Success(courseResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }



}