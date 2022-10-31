package io.hawk.dlp.common

/**
 * A [Occurrence] that provides a locator to specific container name.
 * Such a container name could be the file path + file name in case of a storage bucket.
 * It could also be the database + table name in case of a database.
 * This class therefore aims to abstract occurrences among different vendors.
 *
 * TODO: add sub class table based container occurrence, where table name be read separately.
 * TODO: add sub class file based container occurrence
 */
open class ContainerOccurrence(
    /**
     * A vendor specific locator for the container represented as a string.
     * e.g. gs://test-bucket/folder1/test.csv, or gproject.testdb.users
     */
    val container: String,
): Occurrence