package org.triumers.newsnippetback.Application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.triumers.newsnippetback.Application.dto.UserDTO;
import org.triumers.newsnippetback.common.exception.UserNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @DisplayName("리그 테스트")
    @Test
    void findLeague() throws UserNotFoundException {
        List<UserDTO> userDTOs = userService.findLeague();

        int beforeCorrectCnt = userDTOs.get(0).getCorrectCnt();

        for (UserDTO user : userDTOs) {
            assertTrue(user.getCorrectCnt() <= beforeCorrectCnt);
            beforeCorrectCnt = user.getCorrectCnt();
        }
    }
}