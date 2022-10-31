package io.hawk.dlp.integration

/**
 * Represents a [JobRequest], which [Job] should be directly executed.
 */
class DirectJobRequest(resultTypes: List<ResultType>, content: Content) :
    JobRequest(resultTypes, content)