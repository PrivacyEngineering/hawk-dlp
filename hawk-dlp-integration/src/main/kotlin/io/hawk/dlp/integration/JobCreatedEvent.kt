package io.hawk.dlp.integration

import org.springframework.context.ApplicationEvent

/**
 * Represents a spring event that is fired when a new job is created.
 * Integration implementation should handle this event.
 */
class JobCreatedEvent(job: Job) : ApplicationEvent(job)
