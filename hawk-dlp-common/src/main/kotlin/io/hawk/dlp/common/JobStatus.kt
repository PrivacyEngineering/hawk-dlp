package io.hawk.dlp.common

/**
 * Represents a job status, that can be fetched.
 */
enum class JobStatus {
    /**
     * This is the default status, when a job is created.
     */
    CREATED,

    /**
     * The job is in progress.
     * The integration implementation has found this job and is processing it.
     */
    IN_PROGRESS,

    /**
     * This status is set, when a job is finished.
     */
    COMPLETED,

    /**
     * This status is set, when a job is failed.
     */
    FAILED
}