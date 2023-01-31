package net.bosowski.chattergpt.data.models

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import org.jetbrains.annotations.NotNull
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User
import javax.persistence.*
import java.util.Date

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
class OauthUser(
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, mappedBy = "user")
    var oauthAttributes: List<OauthAttribute>?,

    @ManyToMany
    var oauthAuthorities: List<OauthAuthority>,

    @NotNull
    @Column(unique = true)
    var email: String,

    var firstName: String,
    var lastName: String
) : OAuth2User {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null

    @NotNull
    var availableCredits: Float? = 0f;

    override fun getName(): String {
        return oauthAttributes?.find { it.attributeKey == "name" }?.attributeValue ?: ""
    }

    override fun getAttributes(): Map<String?, String>? {
        return oauthAttributes?.associate { it.attributeKey to it.attributeValue.toString() }
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val foundAuthorities = this.oauthAuthorities?.distinct()
        return foundAuthorities?.toMutableList() ?: ArrayList()
    }

}