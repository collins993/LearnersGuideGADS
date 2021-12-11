package io.github.collins993.learnersguide.api

import okhttp3.Interceptor
import okhttp3.Response

class MyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader(
                "Accept", "application/json, text/plain, */*",
            )
            .addHeader(
                "Authorization",
                "Basic TVFpQTh4Y3NXcDI0SnoyVWltWGZmRFZNOEhwQU5mZzZNUW5wZUNVcjpqSDhHODFoQ2JqYWdoQjNwWGRsbU41TldRR0tHWWR2RnZWRkx3RjVHOEpQdzBGdTRmRjVrZDF4ZDBtZ3JiQzZrS3NoR2hwbVJ6cG5mU2lIQ1RzZDdDNFl5WXg1UmZ0N0FrdXowQ0RYbUI2RkdlRWZGbmZOVkU3Z1dxalFDaGtmYQ=="
            )
            .addHeader("Content-Type", "application/json;charset=utf-8")
            .build()
        return chain.proceed(request)
    }
}