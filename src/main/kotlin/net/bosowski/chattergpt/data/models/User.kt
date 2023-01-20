package net.bosowski.chattergpt.data.models

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import org.jetbrains.annotations.NotNull
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User
import javax.persistence.*
import java.util.Date
import javax.validation.constraints.Size

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
class User(
    @ManyToMany(cascade = [CascadeType.ALL])
    var oauthAuthorities: List<OauthAuthority>? = ArrayList(),
    @ManyToMany(cascade = [CascadeType.ALL])
    var oauthAttributes: List<OauthAttribute>? = ArrayList()
) : OAuth2User {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null

    @NotNull
    var registrationId: String? = null

    @NotNull
    var clientSecret: String? = null

    @NotNull
    @Size(min = 3)
    var email: String? = null


    @NotNull
    var createdAt: Date = Date()


    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun getAttributes(): Map<String?, String>? {
        return oauthAttributes?.associate { it.attributeKey to it.attributeValue.toString() }
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return this.oauthAuthorities?.toMutableList() ?: ArrayList()
    }


}