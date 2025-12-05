package com.eliascoelho911.paymentsdk.gateway.stripe

import com.eliascoelho911.paymentsdk.api.PaymentMethod
import com.eliascoelho911.paymentsdk.api.PaymentRequest
import com.eliascoelho911.paymentsdk.domain.model.CardPayload
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.ParametersBuilder
import io.ktor.http.URLProtocol.Companion.HTTPS
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StripeClient(
    private val configuration: StripeConfiguration = StripeConfiguration.fromEnvironment(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
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

    suspend fun processPayment(
        paymentRequest: PaymentRequest,
        @Suppress("unused") cardPayload: CardPayload
    ): StripePaymentResult =
        withContext(ioDispatcher) {
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
            ).body()
        }
}

private val PaymentMethod.stripeType: String
    get() = when (this) {
        PaymentMethod.CREDIT -> "pm_card_visa"
        PaymentMethod.DEBIT -> "pm_card_visa_debit"
    }
