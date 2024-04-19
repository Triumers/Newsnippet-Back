package org.triumers.newsnippetback.Application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.triumers.newsnippetback.common.exception.UserNotFoundException;
import org.triumers.newsnippetback.domain.aggregate.entity.User;
import org.triumers.newsnippetback.Application.dto.UserDTO;
import org.triumers.newsnippetback.domain.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO findUserByEmail(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email);

        return userToUserDTO(user);
    }

    @Override
    public UserDTO findUserById(int id) throws UserNotFoundException {
        User user = userRepository.findById(id);

        return userToUserDTO(user);
    }

    @Override
    public UserDTO findUserByNickname(String nickname) throws UserNotFoundException {
        User user = userRepository.findByNickname(nickname);

        return userToUserDTO(user);
    }

    @Override
    public UserDTO findByToken() throws UserNotFoundException {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return findUserByEmail(email);
    }

    @Override
    public List<UserDTO> findLeague() throws UserNotFoundException {
        return null;
    }

    private UserDTO userToUserDTO(User user) throws UserNotFoundException {
        if (user == null) {
            throw new UserNotFoundException();
        }

        int rank = userRepository.findRankByCorrectCnt(user.getCorrectCnt());

        return new UserDTO(user.getName(), user.getNickname(), user.getEmail(), user.getUserRole(),
                user.getUserStatus(), user.getSolvedCnt(), user.getCorrectCnt(), rank);
    }
}
