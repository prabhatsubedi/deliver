package com.yetistep.delivr.service.impl;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/6/14
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */

import java.util.*;

import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.model.AuthenticatedUser;
import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.model.UserEntity;
import org.apache.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Custom Spring Authentication and Authorization Class
 */
public class SpringUserDetailsServiceImpl implements UserDetailsService {
    private Logger log = Logger.getLogger(SpringUserDetailsServiceImpl.class);

    private UserDaoService userDao;

    @Override
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {

        log.info("++++++++++ Checking User Name and Password +++++++++++++++++++");
        UserEntity user = userDao.findByUserName(username);

        List<GrantedAuthority> authorities = buildUserAuthority(user.getRole());
        AuthenticatedUser authUser = buildUserForAuthentication(user, authorities);
        log.info("+++++++++++ Successfully User Authenticated ++++++++++++++++");
        return authUser;

    }

    private AuthenticatedUser buildUserForAuthentication(UserEntity user,
                                                         List<GrantedAuthority> authorities) {
        //The Session Value Set Here
        return new AuthenticatedUser(user.getUsername(),
                user.getPassword(), user.getVerifiedStatus(),
                true, true, true, authorities, "Surendra", "Nepal");
    }

    private List<GrantedAuthority> buildUserAuthority(RoleEntity role) {
        Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();
        // Build user's authorities
        try {
            setAuths.add(new SimpleGrantedAuthority(role.getRole()));
            List<GrantedAuthority> Result = new ArrayList<GrantedAuthority>(setAuths);
            return Result;
        } catch (Exception e) {
            throw e;
        }

    }

    public UserDaoService getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDaoService userDao) {
        this.userDao = userDao;
    }

}
