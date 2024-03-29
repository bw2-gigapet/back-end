package com.lambdaschool.usermodel.services;

import com.lambdaschool.usermodel.exceptions.ResourceFoundException;
import com.lambdaschool.usermodel.exceptions.ResourceNotFoundException;
import com.lambdaschool.usermodel.logging.Loggable;
import com.lambdaschool.usermodel.models.*;
import com.lambdaschool.usermodel.repository.EatzRepository;
import com.lambdaschool.usermodel.repository.RoleRepository;
import com.lambdaschool.usermodel.repository.UserRepository;
import com.lambdaschool.usermodel.view.UserNameCountEmails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Loggable
@Transactional
@Service(value = "userService")
public class UserServiceImpl implements UserService
{

    @Autowired
    UserAuditing userAuditing;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EatzRepository eatzRepository;

    public User findUserById(long id) throws ResourceNotFoundException
    {
        return userRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("User id " + id + " not found!"));
    }

    @Override
    public List<User> findByNameContaining(String username,
                                           Pageable pageable)
    {
        return userRepository.findByUsernameContainingIgnoreCase(username.toLowerCase(),
                                                            pageable);
    }

    @Override
    public List<User> findAll(Pageable pageable)
    {
        List<User> list = new ArrayList<>();
        userRepository.findAll(pageable)
                 .iterator()
                 .forEachRemaining(list::add);
        return list;
    }

    @Transactional
    @Override
    public void delete(long id)
    {
        userRepository.findById(id)
                 .orElseThrow(() -> new ResourceNotFoundException("User id " + id + " not found!"));
        userRepository.deleteById(id);
    }

    @Override
    public User findByName(String name)
    {
        User uu = userRepository.findByUsername(name.toLowerCase());
        if (uu == null)
        {
            throw new ResourceNotFoundException("User name " + name + " not found!");
        }
        return uu;
    }

    @Transactional
    @Override
    public User save(User user)
    {
        if (userRepository.findByUsername(user.getUsername()
                                         .toLowerCase()) != null)
        {
            throw new ResourceFoundException(user.getUsername() + " is already taken!");
        }

        User newUser = new User();
        newUser.setUsername(user.getUsername()
                                .toLowerCase());
        newUser.setPasswordNotEncrypt(user.getPassword());
        newUser.setFullname(user.getFullname());
                                  //  .toLowerCase());

        ArrayList<UserRoles> newRoles = new ArrayList<>();
        for (UserRoles ur : user.getUserroles())
        {
            long id = ur.getRole()
                        .getRoleid();
            Role role = roleRepository.findById(id)
                                 .orElseThrow(() -> new ResourceNotFoundException("Role id " + id + " not found!"));
            newRoles.add(new UserRoles(newUser,
                                       role));
        }
        newUser.setUserroles(newRoles);

        for (Useremail ue : user.getUseremails())
        {
            newUser.getUseremails()
                   .add(new Useremail(newUser,
                                      ue.getUseremail()));
        }
        ArrayList<Eatz> newEatz = new ArrayList<>();
     /*   for (Eatz e : user.getUsereatz())
        {
            long id = e.getEatzid();
            Eatz eatz = eatzRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("eatz id " + id + " not found!"));
            newEatz.add(eatz);
        }
        newUser.setUsereatz(newEatz);*/

        return userRepository.save(newUser);
    }
 /*   @Transactional
    @Override
    public User updateEatz(User user,
                       long id) {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        User authenticatedUser = userrepos.findByUsername(authentication.getName());

        if (id == authenticatedUser.getUserid()) {
            User currentUser = findUserById(id);

            if (user.getUsername() != null)
            {
                currentUser.setUsername(user.getUsername()
                        .toLowerCase());
            }

            if (user.getPassword() != null)
            {
                currentUser.setPasswordNotEncrypt(user.getPassword());
            }

            if (user.getPrimaryemail() != null)
            {
                currentUser.setPrimaryemail(user.getPrimaryemail()
                        .toLowerCase());
            }
            if (user.getUsereatz().size() > 0)
            {
                for (Eatz e : user.getUsereatz())
                {
                    currentUser.getUsereatz().add(new Eatz(e.getTitle(),e.getCarbs(),e.getProteins(),e.getFats(),currentUser));
                }
            }
            return userrepos.save(currentUser);
        }else
        {
            throw new ResourceNotFoundException(id + " Not current user");
        }
    }*/
    @Transactional
    @Override
    public User update(User user,
                       long id,
                       boolean isAdmin)
    {
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();

        User authenticatedUser = userRepository.findByUsername(authentication.getName());

        if (id == authenticatedUser.getUserid() || isAdmin)
        {
            User currentUser = findUserById(id);

            if (user.getUsername() != null)
            {
                currentUser.setUsername(user.getUsername()
                                            .toLowerCase());
            }

            if (user.getPassword() != null)
            {
                currentUser.setPasswordNotEncrypt(user.getPassword());
            }

            if (user.getFullname() != null)
            {
                currentUser.setFullname(user.getFullname());
                                              //  .toLowerCase())
            }

            if (user.getUserroles()
                    .size() > 0)
            {
                throw new ResourceFoundException("User Roles are not updated through User. See endpoint POST: users/user/{userid}/role/{roleid}");
            }

            if (user.getUseremails()
                    .size() > 0)
            {
                for (Useremail ue : user.getUseremails())
                {
                    currentUser.getUseremails()
                               .add(new Useremail(currentUser,
                                                  ue.getUseremail()));
                }
            }
            if (user.getUsereatz().size() > 0)
            {
                for (Eatz e : user.getUsereatz())
                {
                    Eatz newEats = new Eatz(e.getTitle(),e.getCarbs(),e.getProteins(),e.getFats());
                    newEats.setUser(currentUser);
                    currentUser.getUsereatz().add(newEats);
                }
            }
            return userRepository.save(currentUser);
        } else
        {
            throw new ResourceNotFoundException(id + " Not current user");
        }
    }

    @Transactional
    @Override
    public void deleteUserRole(long userid,
                               long roleid)
    {
        userRepository.findById(userid)
                 .orElseThrow(() -> new ResourceNotFoundException("User id " + userid + " not found!"));
        roleRepository.findById(roleid)
                 .orElseThrow(() -> new ResourceNotFoundException("Role id " + roleid + " not found!"));

        if (roleRepository.checkUserRolesCombo(userid,
                                          roleid)
                     .getCount() > 0)
        {
            roleRepository.deleteUserRoles(userid,
                                      roleid);
        } else
        {
            throw new ResourceNotFoundException("Role and User Combination Does Not Exists");
        }
    }

    @Transactional
    @Override
    public void addUserRole(long userid,
                            long roleid)
    {
        userRepository.findById(userid)
                 .orElseThrow(() -> new ResourceNotFoundException("User id " + userid + " not found!"));
        roleRepository.findById(roleid)
                 .orElseThrow(() -> new ResourceNotFoundException("Role id " + roleid + " not found!"));

        if (roleRepository.checkUserRolesCombo(userid,
                                          roleid)
                     .getCount() <= 0)
        {
            roleRepository.insertUserRoles(userAuditing.getCurrentAuditor()
                                                  .get(),
                                      userid,
                                      roleid);
        } else
        {
            throw new ResourceFoundException("Role and User Combination Already Exists");
        }
    }

    @Override
    public List<UserNameCountEmails> getCountUserEmails()
    {
        return userRepository.getCountUserEmails();
    }
}
