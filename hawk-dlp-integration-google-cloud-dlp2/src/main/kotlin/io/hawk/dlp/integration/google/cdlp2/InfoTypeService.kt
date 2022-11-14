package io.hawk.dlp.integration.google.cdlp2

import io.hawk.dlp.common.InfoType
import org.springframework.stereotype.Service

@Service
class InfoTypeService {
    fun translateGoogleInfoType(type: String): InfoType = when (type.uppercase()) {
        "AWS_CREDENTIALS", "GCP_CREDENTIALS", "AZURE_CREDENTIALS" -> InfoType.CREDENTIALS
        "LOCATION_COORDINATES" -> InfoType.LOCATION
        "PERSON_NAME", "FEMALE_NAME", "FIRST_NAME", "MALE_NAME", "LAST_NAME" -> InfoType.NAME
        "STREET_ADDRESS" -> InfoType.ADDRESS
        else -> InfoType.values().firstOrNull { it.name == type.uppercase() } ?: InfoType.UNKNOWN
    }
}