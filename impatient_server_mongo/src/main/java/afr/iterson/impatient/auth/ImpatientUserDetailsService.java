package afr.iterson.impatient.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import afr.iterson.impatient.model.ImpatientUser;
import afr.iterson.impatient.repository.ImpatientUserRepository;

/**
 * Thanks to @Patty Harris for showing the way on this
 * @author ap
 *
 */

public class ImpatientUserDetailsService implements UserDetailsService
{
	/**
	 * User cache to store the registered users.
	 */
	@Autowired
	private ImpatientUserRepository mUserRepository;

	/**
	 * Default constructor is always required for Spring injection.
	 */
	public ImpatientUserDetailsService()
	{
	}

	/**
	 * Allows bulk input of users
	 * 
	 * @param users
	 */
	public void loadUsers(Collection<ImpatientUser> users)
	{
		for (ImpatientUser user : users)
		{
			ImpatientUser tmpUser = new ImpatientUser(user.getUsername(), user.getPassword(), user.isEnabled(),
					user.getAuthorities());
			mUserRepository.save(tmpUser);
		}

	}

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException
	{

		ImpatientUser user = mUserRepository.findByUsername(username);
		List<GrantedAuthority> authorities = buildUserAuthority(user.getRoles());

		return buildUserForAuthentication(user, authorities);

	}

	/**
	 * Converts ImpatientUser user to
	 * org.springframework.security.core.userdetails.User
	 * 
	 * @param user
	 * @param authorities
	 * @return
	 */
	private org.springframework.security.core.userdetails.User buildUserForAuthentication(ImpatientUser user,
			List<GrantedAuthority> authorities)
	{
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				user.isEnabled(), true, true, true, authorities);
	}

	/**
	 * Build the user authority collection
	 * 
	 * @param userRoles
	 * @return
	 */
	private List<GrantedAuthority> buildUserAuthority(Set<String> userRoles)
	{

		Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();
		// Build user's authorities
		for (String userRole : userRoles)
		{
			setAuths.add(new SimpleGrantedAuthority(userRole));
		}
		List<GrantedAuthority> Result = new ArrayList<GrantedAuthority>(setAuths);
		return Result;
	}

}
