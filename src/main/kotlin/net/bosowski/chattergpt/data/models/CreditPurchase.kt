package net.bosowski.chattergpt.data.models

import lombok.Data
import net.bosowski.chattergpt.data.models.authentication.OauthUser
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Data
@Entity
class CreditPurchase(
    @ManyToOne
    @JoinColumn(name = "oauth_user_id")
    var oauthUser: OauthUser,
    @NotNull
    var credits: Float,
    @NotNull
    var sessionId: String? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @NotNull
    val date = Date()

    var successful: Boolean? = null
}