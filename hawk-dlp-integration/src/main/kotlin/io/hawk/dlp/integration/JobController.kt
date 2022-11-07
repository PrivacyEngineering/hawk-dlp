package io.hawk.dlp.integration

import io.hawk.dlp.common.Result
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Validated
@RestController
@RequestMapping("/api/v1/jobs")
class JobController(
    private val jobService: JobService
) {
    @PostMapping("/api/v1/jobs/direct")
    fun createDirectJob(@Valid @RequestBody request: DirectJobRequest): Job =
        jobService.createJob(request)

    @GetMapping("/api/v1/jobs/{id}")
    fun describeJob(@PathVariable id: UUID): Job =
        jobService.getJob(id)

    @GetMapping("/api/v1/jobs/{id}/results/{resultId}")
    fun getJobResult(@PathVariable id: UUID, @PathVariable resultId: UUID): Result =
        jobService
            .getJob(id)
            .results
            ?.entries
            ?.firstOrNull { it.key.id == resultId }
            ?.value
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Result not found")
}