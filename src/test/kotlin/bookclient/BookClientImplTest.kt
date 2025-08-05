package bookclient

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class BookClientImplTest {
    private val client = BookClientImpl()

    @Test
     fun testGetAllBook() = runBlocking {
        val books = client.getAllBook()
        println("Books: $books")
        assertNotNull(books)
        assertTrue(books.isEmpty(), "Books list should be empty when no books exist")
    }

    @Test
    fun testGetBookDetail() = runBlocking {
        // Replace with a valid bookId from your API, or use a dummy to test not found
        val bookId = "Uvh6KtHbsu_kP79K"
        val bookDetail = client.getBookDetail(bookId)
        println("Book Detail: $bookDetail")
        // You can assertNull(bookDetail) for not found, or assertNotNull for a valid id
    }

    @Test
    fun testAddBook() = runBlocking {
        val bookId = client.addBook(
            name = "Buku A",
            year = 2010,
            author = "John Doe",
            summary = "Lorem ipsum dolor sit amet",
            publisher = "Dicoding Indonesia",
            pageCount = 100,
            readPage = 25,
            reading = false
        )
        println("Added Book ID: $bookId")
        assertNotNull(bookId, "Book ID should not be null when book is successfully added")
    }

    @Test
    fun testAddBookAndGetDetail() = runBlocking {
        val bookId = client.addBook(
            name = "Buku A",
            year = 2010,
            author = "John Doe",
            summary = "Lorem ipsum dolor sit amet",
            publisher = "Dicoding Indonesia",
            pageCount = 100,
            readPage = 25,
            reading = false
        )
        println("Added Book ID: $bookId")
        assertNotNull(bookId, "Book ID should not be null when book is successfully added")

        val bookDetail = client.getBookDetail(bookId!!)
        println("Book Detail: $bookDetail")
        assertNotNull(bookDetail, "Book detail should not be null for a valid bookId")
        assertEquals("Buku A", bookDetail?.name)
        assertEquals(2010, bookDetail?.year)
        assertEquals("John Doe", bookDetail?.author)
        assertEquals("Lorem ipsum dolor sit amet", bookDetail?.summary)
        assertEquals("Dicoding Indonesia", bookDetail?.publisher)
        assertEquals(100, bookDetail?.pageCount)
        assertEquals(25, bookDetail?.readPage)
        assertEquals(false, bookDetail?.reading)
    }


}
