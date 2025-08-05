package bookclient

interface BookClient {
    suspend fun getAllBook(): List<BookSummary>
    suspend fun getBookDetail(bookId: String): BookDetail?
    suspend fun addBook(
        name: String,
        year: Int,
        author: String?,
        summary: String?,
        publisher: String?,
        pageCount: Int?,
        readPage: Int,
        reading: Boolean
    ): String? // returns bookId if success

    suspend fun updateBook(
        bookId: String,
        name: String?,
        year: Int?,
        author: String?,
        summary: String?,
        publisher: String?,
        pageCount: Int?,
        readPage: Int?,
        reading: Boolean?
    ): Boolean // returns true if success

    suspend fun deleteBook(bookId: String): Boolean // returns true if success
}