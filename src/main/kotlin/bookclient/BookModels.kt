package bookclient

import kotlinx.serialization.Serializable

@Serializable
data class BookSummary(
    val id: String,
    val name: String,
    val publisher: String
)

@Serializable
data class BookListData(
    val books: List<BookSummary>
)

@Serializable
data class BookListResponse(
    val status: String,
    val data: BookListData
)

@Serializable
 data class BookDetail(
    val id: String,
    val name: String,
    val year: Int,
    val author: String,
    val summary: String,
    val publisher: String,
    val pageCount: Int,
    val readPage: Int,
    val finished: Boolean,
    val reading: Boolean,
    val insertedAt: String,
    val updatedAt: String
)

@Serializable
 data class BookDetailData(
    val book: BookDetail
)

@Serializable
 data class BookDetailResponse(
    val status: String,
    val data: BookDetailData
)

@Serializable
 data class ApiResponse<T>(
    val status: String,
    val message: String? = null,
    val data: T? = null
)

