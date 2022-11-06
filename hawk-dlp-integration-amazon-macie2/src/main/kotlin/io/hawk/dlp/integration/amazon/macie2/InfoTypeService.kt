package io.hawk.dlp.integration.amazon.macie2

import io.hawk.dlp.common.InfoType
import org.springframework.stereotype.Service

@Service
class InfoTypeService {
    fun translateAmazonInfoType(type: String): InfoType = when (type.uppercase()) {
        "AWS_CREDENTIALS" -> InfoType.CREDENTIALS
        "OPENSSH_PRIVATE_KEY", "PGP_PRIVATE_KEY", "PKCS", "PUTTY_PRIVATE_KEY" -> InfoType.ENCRYPTION_KEY
        "LATITUDE_LONGITUDE" -> InfoType.LOCATION
        else -> if (type.uppercase().endsWith("_PASSPORT_NUMBER")) {
            InfoType.PASSPORT_NUMBER
        } else if (type.uppercase().endsWith("_PHONE_NUMBER")) {
            InfoType.PHONE_NUMBER
        } else {
            InfoType.values().firstOrNull { it.name == type.uppercase() } ?: InfoType.UNKNOWN
        }

    }
}