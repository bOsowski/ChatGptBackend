package net.bosowski.chattergpt.data.models

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import org.jetbrains.annotations.NotNull
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.user.OAuth2User
import javax.persistence.*
import java.util.Date

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
class OauthUser : OidcUser {
    @OneToMany(cascade = [CascadeType.ALL])
    var oauthAttributes: List<OauthAttribute> = ArrayList()

    @OneToMany(cascade = [CascadeType.ALL])
    var oauthAuthorities: List<OauthAuthority> = ArrayList()

    @Id
    @NotNull
    var id: String? = null

    @NotNull
    var availableCredits: Float = 0f

    @OneToOne(cascade = [CascadeType.ALL])
    var oauthToken: OauthToken? = null

    constructor(
        oauthAttributes: List<OauthAttribute>,
        oauthAuthorities: List<OauthAuthority>,
    ) : this() {
        this.oauthAttributes = oauthAttributes
        this.oauthAuthorities = oauthAuthorities
    }

    constructor()

    override fun getName(): String {
        return oauthAttributes.find { it.attributeKey == "name" }?.attributeValue ?: ""
    }

    override fun getAttributes(): Map<String?, String>? {
        return oauthAttributes.associate { it.attributeKey to it.attributeValue.toString() }
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return this.oauthAuthorities.distinct().toMutableList()
    }

    override fun getClaims(): MutableMap<String?, Any> {
        return oauthAttributes.associate { it.attributeKey to (it.attributeValue as Any) }.toMutableMap()
    }

    override fun getUserInfo(): OidcUserInfo {
        return OidcUserInfo(this.attributes)
    }

    override fun getIdToken(): OidcIdToken {
        return OidcIdToken(
            this.oauthToken?.tokenValue,
            this.oauthToken?.issuedAt?.toInstant(),
            this.oauthToken?.expiresAt?.toInstant(),
            this.attributes
        )
    }
}