package uk.gov.justice.digital.hmpps.prisonerpayorchestratorapi.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.expression.BeanFactoryResolver
import org.springframework.expression.spel.SpelEvaluationException
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.method.HandlerMethod
import uk.gov.justice.digital.hmpps.prisonerpayapi.common.asListOfType

@Configuration
class OpenApiConfiguration(buildProperties: BuildProperties) {

  @Autowired
  private lateinit var context: ApplicationContext

  private val version: String = buildProperties.version

  @Bean
  fun customOpenAPI(): OpenAPI = OpenAPI()
    .servers(
      listOf(
        Server().url("https://prisoner-pay-orchestrator-api-dev.hmpps.service.justice.gov.uk").description("Development"),
        Server().url("https://prisoner-pay-orchestrator-api-preprod.hmpps.service.justice.gov.uk").description("Pre-Production"),
        Server().url("https://prisoner-pay-orchestrator-api.hmpps.service.justice.gov.uk").description("Production"),
        Server().url("http://localhost:8080").description("Local"),
      ),
    )
    .tags(
      listOf(),
    )
    .info(
      Info().title("HMPPS Prisoner Pay Orchestrator Api").version(version)
        .contact(Contact().name("HMPPS Digital Studio").email("feedback@digital.justice.gov.uk")),
    )
    .components(
      Components().addSecuritySchemes(
        "bearer-jwt",
        SecurityScheme()
          .type(SecurityScheme.Type.HTTP)
          .scheme("bearer")
          .bearerFormat("JWT"),
      ),
    )
    .addSecurityItem(SecurityRequirement().addList("bearer-jwt"))

  @Bean
  fun authorizationCustomizer(): OperationCustomizer = OperationCustomizer { operation: Operation, handlerMethod: HandlerMethod ->
    val preAuthorizeValue = handlerMethod.getMethodAnnotation(PreAuthorize::class.java)?.value
      ?: handlerMethod.beanType.getAnnotation(PreAuthorize::class.java)?.value

    preAuthorizeValue?.let { expression ->
      val spelParser = SpelExpressionParser()
      val parsedExpression = spelParser.parseExpression(expression)
      val spelContext = StandardEvaluationContext().apply {
        beanResolver = BeanFactoryResolver(context)
        setRootObject(
          object {
            fun hasRole(role: String) = listOf(role)
            fun hasAnyRole(vararg roles: String) = roles.toList()
          },
        )
      }

      val roles = try {
        (parsedExpression.getValue(spelContext) as List<*>).asListOfType<String>()
      } catch (e: SpelEvaluationException) {
        emptyList()
      }

      if (roles.isNotEmpty()) {
        val rolesDescription = roles.joinToString(prefix = "* ", separator = "\n* ")
        operation.description =
          "${operation.description ?: ""}\n\nRequires one of the following roles:\n$rolesDescription"
      }
    }
    operation
  }
}
