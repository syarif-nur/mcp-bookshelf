package bookclient

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.header
import kotlinx.serialization.Serializable


@Serializable
data class AddBookRequest(
    val name: String?,
    val year: Int?,
    val author: String?,
    val summary: String?,
    val publisher: String?,
    val pageCount: Int?,
    val readPage: Int?,
    val reading: Boolean?
)

@Serializable
data class AddBookResponseData(val bookId: String)
@Serializable
data class AddBookResponse(val status: String, val message: String, val data: AddBookResponseData?)

@Serializable
data class UpdateBookRequest(
    val name: String?,
    val year: Int?,
    val author: String?,
    val summary: String?,
    val publisher: String?,
    val pageCount: Int?,
    val readPage: Int?,
    val reading: Boolean?
)

@Serializable
data class UpdateBookResponse(val status: String, val message: String)

@Serializable
data class DeleteBookResponse(val status: String, val message: String)

class BookClientImpl: BookClient {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    private val bridgeUrl = "http://localhost:9000"


    override suspend fun getAllBook(): List<BookSummary> {
        val response = client.get("$bridgeUrl/books")
        val bookListResponse = response.body<BookListResponse>()
        return bookListResponse.data.books.map {
            BookSummary(
                id = it.id,
                name = it.name,
                publisher = it.publisher
            )
        }
    }


    override suspend fun getBookDetail(bookId: String): BookDetail? {
        val response = client.get("$bridgeUrl/books/$bookId")
        return try {
            val detailResponse = response.body<BookDetailResponse>()
            detailResponse.data.book
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addBook(
        name: String,
        year: Int,
        author: String?,
        summary: String?,
        publisher: String?,
        pageCount: Int?,
        readPage: Int,
        reading: Boolean
    ): String? {
        try {
            val requestBody = AddBookRequest(
                name = name,
                year = year,
                author = author,
                summary = summary,
                publisher = publisher,
                pageCount = pageCount,
                readPage = readPage,
                reading = reading
            )
            val response = client.post("$bridgeUrl/books") {
                setBody(requestBody)
                header("Content-Type", "application/json")
            }
            val addBookResponse = response.body<AddBookResponse>()
            if (addBookResponse.status == "success" && addBookResponse.data != null) {
                return addBookResponse.data.bookId
            }
        } catch (e: ResponseException) {
            // handle 400/other errors gracefully
            println("AddBook error: ${e.response.status}")
            return null
        } catch (e: Exception) {
            println("AddBook error: ${e.message}")
            return null
        }
        return null
    }

    override suspend fun updateBook(
        bookId: String,
        name: String?,
        year: Int?,
        author: String?,
        summary: String?,
        publisher: String?,
        pageCount: Int?,
        readPage: Int?,
        reading: Boolean?
    ): Boolean {
        try {
            val requestBody = UpdateBookRequest(
                name = name,
                year = year,
                author = author,
                summary = summary,
                publisher = publisher,
                pageCount = pageCount,
                readPage = readPage,
                reading = reading
            )
            val response = client.put("$bridgeUrl/books/$bookId") {
                setBody(requestBody)
            }
            val updateBookResponse = response.body<UpdateBookResponse>()
            return updateBookResponse.status == "success"
        } catch (e: ResponseException) {
            return false
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun deleteBook(bookId: String): Boolean {
        try {
            val response = client.delete("$bridgeUrl/books/$bookId")
            val deleteBookResponse = response.body<DeleteBookResponse>()
            return deleteBookResponse.status == "success"
        } catch (e: ResponseException) {
            return false
        } catch (e: Exception) {
            return false
        }
    }
}