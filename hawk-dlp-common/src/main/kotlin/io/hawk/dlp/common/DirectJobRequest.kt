package io.hawk.dlp.common

/**
 * Represents a [JobRequest], which [Job] should be directly executed.
 */
class DirectJobRequest(goals: List<Goal>, content: Content, sourceReference: String?) :
    JobRequest("direct", goals, content, sourceReference)