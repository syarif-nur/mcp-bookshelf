package bookmcp

import bookclient.BookClient
import bookclient.BookClientImpl
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

fun main() {
    val server: Server = createServer()
    val stdioServerTransport = StdioServerTransport(
        System.`in`.asSource().buffered(),
        System.out.asSink().buffered()
    )
    runBlocking {
        val job = Job()
        server.onClose { job.complete() }
        server.connect(stdioServerTransport)
        job.join()
    }
}

fun createServer(): Server {
    val info = Implementation(
        "Bookshelf MCP",
        "1.0.0"
    )
    val options = ServerOptions(
        capabilities = ServerCapabilities(tools = ServerCapabilities.Tools(true))
    )
    val server = Server(info, options)
    val bookClient: BookClient = BookClientImpl()

    // List all books
    server.addTool(
        "list-books",
        "Returns all books with their names"
    ) {
        try {
            // Use runBlocking to handle the suspend function
            val books = runBlocking {
                bookClient.getAllBook()
            }
            CallToolResult(
                listOf(TextContent("Books: ${books.joinToString(", ")}"))
            )
        } catch (e: Exception) {
            CallToolResult(
                listOf(TextContent("Error listing books: ${e.message}"))
            )
        }
    }

    // Get book detail
    val getBookInputSchema = Tool.Input(
        buildJsonObject {
            put("bookId", buildJsonObject {
                put("type", "string")
                put("description", "The ID of the book to get details for")
            }
            )
        }
    )

    server.addTool(
        "get-detail-book",
        "Get detailed information about a specific book",
        getBookInputSchema
    ) { input ->
        try {
            val bookId = input.arguments["bookId"]!!.jsonPrimitive.content

            // Use runBlocking to handle the suspend function
            val bookDetail = runBlocking {
                bookClient.getBookDetail(bookId)
            }

            if (bookDetail != null) {
                CallToolResult(
                    listOf(TextContent("Book Details: $bookDetail"))
                )
            } else {
                CallToolResult(
                    listOf(TextContent("Book not found with ID: $bookId"))
                )
            }
        } catch (e: Exception) {
            CallToolResult(
                listOf(TextContent("Error getting book: ${e.message}"))
            )
        }
    }

    // Add book
    val addBookInputSchema = Tool.Input(
        buildJsonObject {

            put("name", buildJsonObject {
                put("type", "string")
                put("description", "The name of the book")
            })
            put("year", buildJsonObject {
                put("type", "integer")
                put("description", "The publication year")
            })
            put("author", buildJsonObject {
                put("type", "string")
                put("description", "The author of the book")
            })
            put("summary", buildJsonObject {
                put("type", "string")
                put("description", "A summary of the book")
            })
            put("publisher", buildJsonObject {
                put("type", "string")
                put("description", "The publisher of the book")
            })
            put("pageCount", buildJsonObject {
                put("type", "integer")
                put("description", "The number of pages")
            })
        }
    )

    server.addTool(
        "add-book",
        "Add a new book to the collection",
        addBookInputSchema
    ) { input ->
        try {
            val name = input.arguments["name"]!!.jsonPrimitive.content
            val year = input.arguments["year"]!!.jsonPrimitive.content.toInt()
            val author = input.arguments["author"]?.jsonPrimitive?.content
            val summary = input.arguments["summary"]?.jsonPrimitive?.content
            val publisher = input.arguments["publisher"]?.jsonPrimitive?.content
            val pageCount = input.arguments["pageCount"]?.jsonPrimitive?.intOrNull

            // Use runBlocking to handle the suspend function
            val bookId = runBlocking {
                bookClient.addBook(name, year, author, summary, publisher, pageCount, 0, false)
            }

            if (bookId != null) {
                CallToolResult(
                    listOf(TextContent("Book added successfully with ID: $bookId"))
                )
            } else {
                CallToolResult(
                    listOf(TextContent("Failed to add book"))
                )
            }
        } catch (e: Exception) {
            CallToolResult(
                listOf(TextContent("Error adding book: ${e.message}"))
            )
        }
    }

    // Update book
    val updateBookInputSchema = Tool.Input(
        buildJsonObject {


            put("bookId", buildJsonObject {
                put("type", "string")
                put("description", "The ID of the book to update")
            })
            put("name", buildJsonObject {
                put("type", "string")
                put("description", "The new name of the book")
            })
            put("year", buildJsonObject {
                put("type", "integer")
                put("description", "The new publication year")
            })
            put("author", buildJsonObject {
                put("type", "string")
                put("description", "The new author of the book")
            })
            put("summary", buildJsonObject {
                put("type", "string")
                put("description", "The new summary of the book")
            })
            put("publisher", buildJsonObject {
                put("type", "string")
                put("description", "The new publisher of the book")
            })
            put("pageCount", buildJsonObject {
                put("type", "integer")
                put("description", "The new number of pages")
            })
            put("readPage", buildJsonObject {
                put("type", "integer")
                put("description", "Number of pages read")
            })
            put("reading", buildJsonObject {
                put("type", "boolean")
                put("description", "Whether currently reading this book")
            })
        }
    )

    server.addTool(
        "update-book",
        "Update an existing book",
        updateBookInputSchema
    ) { input ->
        try {
            val bookId = input.arguments["bookId"]!!.jsonPrimitive.content
            val name = input.arguments["name"]?.jsonPrimitive?.content
            val year = input.arguments["year"]?.jsonPrimitive?.intOrNull
            val author = input.arguments["author"]?.jsonPrimitive?.content
            val summary = input.arguments["summary"]?.jsonPrimitive?.content
            val publisher = input.arguments["publisher"]?.jsonPrimitive?.content
            val pageCount = input.arguments["pageCount"]?.jsonPrimitive?.intOrNull
            val readPage = input.arguments["readPage"]?.jsonPrimitive?.intOrNull
            val reading = input.arguments["reading"]?.jsonPrimitive?.content?.toBoolean()

            // Use runBlocking to handle the suspend function
            val success = runBlocking {
                bookClient.updateBook(bookId, name, year, author, summary, publisher, pageCount, readPage, reading)
            }

            if (success) {
                CallToolResult(
                    listOf(TextContent("Book updated successfully"))
                )
            } else {
                CallToolResult(
                    listOf(TextContent("Failed to update book"))
                )
            }
        } catch (e: Exception) {
            CallToolResult(
                listOf(TextContent("Error updating book: ${e.message}"))
            )
        }
    }

    // Delete book
    val deleteBookInputSchema = Tool.Input(
        buildJsonObject {
            put("bookId", buildJsonObject {
                put("type", "string")
                put("description", "The ID of the book to delete")
            })
        }
    )

    server.addTool(
        "delete-book",
        "Delete a book from the collection",
        deleteBookInputSchema
    ) { input ->
        try {
            val bookId = input.arguments["bookId"]!!.jsonPrimitive.content

            // Use runBlocking to handle the suspend function
            val success = runBlocking {
                bookClient.deleteBook(bookId)
            }

            if (success) {
                CallToolResult(
                    listOf(TextContent("Book deleted successfully"))
                )
            } else {
                CallToolResult(
                    listOf(TextContent("Failed to delete book"))
                )
            }
        } catch (e: Exception) {
            CallToolResult(
                listOf(TextContent("Error deleting book: ${e.message}"))
            )
        }
    }

    return server
}
