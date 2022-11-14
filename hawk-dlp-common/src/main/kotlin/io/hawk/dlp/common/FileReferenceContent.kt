package io.hawk.dlp.common

import jakarta.validation.constraints.Size

/**
 * A [ReferenceContent], that a carries a reference to one or multiple files.
 */
data class FileReferenceContent(
    /**
     * The list of file-urls that should be analyzed.
     * A file-url can be a bucket, e.g. gs://bucket-name/path/to/file
     */
    @Size(min = 1, max = 20)
    val files: List<String>
) : ReferenceContent