package io.hawk.dlp.common

/**
 * A [Occurrence] that provides a locator to specific container name.
 * For generalized vendor-independent identification, we provide the following patterns:
 * - For file occurrences, [volume] + [filePath] can be used to identify the file uniquely.
 * - For database occurrences, [database] + [table] can be used to identify the table uniquely.
 * This class therefore aims to abstract occurrences among different vendors.
 * Properties of this class try to describe, the location of the data in a vendor-agnostic way.
 * Subclasses may add properties to locate the specific data inside this table / file etc.
 */
interface ContainerOccurrence : Occurrence {
    /**
     * A vendor specific locator for the container represented as a string.
     * e.g. gs://test-bucket/folder1/test.csv, or gproject.testdb.users
     */
    val container: String

    /**
     * Part of the [container], that describes the location where the file (system) of the file is
     * located at. This might be a bucket name.
     *
     * In case of gs://test-bucket/folder1/test.csv, this property would be gs://test-bucket.
     *
     * Only present if the occurrence represents a file.
     */
    val volume: String?

    /**
     * Part of the [container], that describes the path of the file in the respective volume.
     *
     * In case of gs://test-bucket/folder1/test.csv, this property would be /folder1/test.csv.
     *
     * Only present if the occurrence represents a file.
     */
    val filePath: String?

    /**
     * Part of the [container], that describes the database / the prefix of the table.
     * Meaning stuff like database name, schema, project etc.
     *
     * In case of gproject.testdb.users, this property would be gproject.testdb.
     *
     * Only present if the occurrence represents a database.
     */
    val database: String?

    /**
     * Part of the [container], that describes the name of the table.
     *
     * In case of gproject.testdb.users, this property would be users.
     *
     * Only present if the occurrence represents a database.
     */
    val table: String?
}