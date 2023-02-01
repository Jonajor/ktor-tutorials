package com.example.routes

import com.example.models.Customer
import com.example.models.customerStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.customerRouting() {
    route("/customer") {
        get {
            if (customerStorage.isNotEmpty()) {
                call.respond(customerStorage)
            } else {
                call.respondText("No customers found", status = HttpStatusCode.OK)
            }
        }

        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id", status = HttpStatusCode.BadRequest
            )
            val customer = customerStorage.find { it.id == id } ?: return@get call.respondText(
                "No customer with id $id", status = HttpStatusCode.NotFound
            )
            call.respond(customer)
        }

        post {
            val customer = call.receive<Customer>()
            customerStorage.add(customer)
            call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
        }

        put("{id?}/{firsName}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val name = call.parameters["firsName"] ?: return@put call.respond(HttpStatusCode.BadRequest)

            customerStorage.find { it.id == id } ?: return@put call.respondText(
                "No customer with id $id", status = HttpStatusCode.NotFound
            )

            customerStorage.replaceAll { it.copy(firstName = name) }
            call.respondText("Customer updated correctly", status = HttpStatusCode.OK)
        }

        delete("{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (customerStorage.removeIf { it.id == id }) {
                call.respondText("Customer removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}