package io.hawk.dlp.integration

import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
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
    fun getJob(@PathVariable id: UUID): Job =
        jobService.getJob(id)
}