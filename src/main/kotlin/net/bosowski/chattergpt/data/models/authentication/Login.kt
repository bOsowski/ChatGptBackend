package net.bosowski.chattergpt.data.models.authentication

import lombok.AllArgsConstructor
import lombok.Data
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Data
@AllArgsConstructor
class Login(
    @ManyToOne
    @JoinColumn(name = "oauth_user_id")
    var oauthUser: OauthUser? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @NotNull
    val date = Date()
}