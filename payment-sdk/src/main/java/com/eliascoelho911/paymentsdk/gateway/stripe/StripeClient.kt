package com.eliascoelho911.paymentsdk.gateway.stripe

import com.eliascoelho911.paymentsdk.api.PaymentMethod
import com.eliascoelho911.paymentsdk.api.PaymentRequest
import com.eliascoelho911.paymentsdk.api.PaymentStatus
import com.eliascoelho911.paymentsdk.domain.model.CardPayload
import com.eliascoelho911.paymentsdk.gateway.PaymentClient
import com.eliascoelho911.paymentsdk.gateway.stripe.mapper.getPaymentStatus
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.URLProtocol.Companion.HTTPS
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StripeClient(
    private val configuration: StripeConfiguration = StripeConfiguration.fromEnvironment(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PaymentClient {
    private val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(stripeJson)
        }
        defaultRequest {
            url {
                host = "api.stripe.com"
                protocol = HTTPS
            }
            header(HttpHeaders.Authorization, "Bearer ${configuration.secretKey}")
        }
    }

    override suspend fun processPayment(
        paymentRequest: PaymentRequest,
        @Suppress("unused") cardPayload: CardPayload
    ): PaymentStatus = withContext(ioDispatcher) {
        fun ParametersBuilder.appendDefaultValues() {
            append("capture_method", "automatic")
            append("confirm", true.toString())
            append("automatic_payment_methods[enabled]", true.toString())
            append("automatic_payment_methods[allow_redirects]", "never")
            append("customer", configuration.defaultCustomerId)
        }

        fun ParametersBuilder.appendPaymentMethod(method: PaymentMethod) {
            // In a real implementation, you would create a PaymentMethod using the cardPayload details.
            append("payment_method", method.stripeType)
        }

        httpClient.submitForm(
            url = "/v1/payment_intents",
            formParameters = parameters {
                append("amount", paymentRequest.amountCents.toString())
                append("currency", paymentRequest.currency.name.lowercase())
                appendDefaultValues()
                appendPaymentMethod(paymentRequest.method)
            }
        ).body<StripePaymentResult>().getPaymentStatus()
    }
}

private val PaymentMethod.stripeType: String
    get() = when (this) {
        PaymentMethod.CREDIT -> "pm_card_visa"
        PaymentMethod.DEBIT -> "pm_card_visa_debit"
    }
