package afr.iterson.impatient.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class ImpatientUser implements UserDetails
{

	private static final long serialVersionUID = 1L;

	@Id
	private String username = null;

	private String password = null;

	private Set<String> roles = new HashSet<String>();

	private boolean enabled = true;

	@JsonIgnore
	private boolean authorized;

	@SuppressWarnings({ "unchecked", "unused" })
	private ImpatientUser(String username, String password, boolean enabled)
	{
		this(username, password, enabled, Collections.EMPTY_LIST);
	}

	// Default constructor
	public ImpatientUser()
	{
	}

	public ImpatientUser(String username, String password, boolean enabled, String... authorities)
	{
		this(username, password, enabled, AuthorityUtils.createAuthorityList(authorities));
	}

	public ImpatientUser(String username, String password, boolean enabled, Collection<GrantedAuthority> authorities)
	{
		super();
		this.username = username;
		this.password = password;
		this.roles = buildUserRolesFromAuthorities(authorities);
		this.enabled = enabled;
	}

	public static ImpatientUser create(String username, String password, boolean enabled,
			Collection<GrantedAuthority> authorities)
	{
		return new ImpatientUser(username, password, enabled, authorities);
	}

	public static ImpatientUser create(String username, String password, boolean enabled, String... authorities)
	{
		return new ImpatientUser(username, password, enabled, authorities);
	}

	public Set<String> getRoles()
	{
		return roles;
	}

	public Collection<GrantedAuthority> getAuthorities()
	{
		return AuthorityUtils.createAuthorityList(StringUtils.arrayToCommaDelimitedString(roles.toArray()));
	}

	private Set<String> buildUserRolesFromAuthorities(Collection<GrantedAuthority> authorities)
	{

		if (authorities == null || authorities.size() == 0)
		{
			return new HashSet<>();
		} else
		{
			Set<String> set = new HashSet<String>(authorities.size());

			for (GrantedAuthority authority : authorities)
			{
				set.add(authority.getAuthority());
			}

			return set;
		}
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getUsername()
	{
		return username;
	}

	@Override
	public boolean isAccountNonExpired()
	{
		return true;
	}

	@Override
	public boolean isAccountNonLocked()
	{
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired()
	{
		return true;
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	@JsonProperty
	public boolean isAuthorized()
	{
		return authorized;
	}

	@JsonIgnore
	public void setAuthorized(boolean authorized)
	{
		this.authorized = authorized;
	}

	/**
	 * When username and password are equal ImpatientUsers are the same
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ImpatientUser)
		{

			ImpatientUser other = (ImpatientUser) obj;

			return Objects.equal(username, other.username) && Objects.equal(password, other.password);
		} else
		{
			return false;
		}
	}

}
