package net.bosowski.chattergpt.data.models

import lombok.Data
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Data
@Entity
class CreditPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @NotNull
    val date = Date()

    @NotNull
    var credits: Float? = null

    @ManyToOne
    @JoinColumn(name = "oauth_user_id")
    var oauthUser: OauthUser? = null
}