package net.bosowski.chattergpt.data.models.authentication

import lombok.Data
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