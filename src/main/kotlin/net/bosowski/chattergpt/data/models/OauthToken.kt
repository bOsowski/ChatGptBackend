package net.bosowski.chattergpt.data.models

import lombok.Data
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import java.time.Instant
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Data
@Entity
class OauthToken(
    @Column(columnDefinition = "text")
    @NotNull
    var tokenValue: String,

    @NotNull
    var issuedAt: Date,

    var expiresAt: Date?,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    var id: Long? = null
}