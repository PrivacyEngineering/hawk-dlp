package io.hawk.dlp.integration

import io.hawk.dlp.common.Job
import org.springframework.context.ApplicationEvent

/**
 * Represents a spring event that is fired when a new job is created.
 * Integration implementation should handle this event.
 */
class JobCreatedEvent(val job: Job) : ApplicationEvent(job)
